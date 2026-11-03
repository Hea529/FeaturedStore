package com.preferredmall.im.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.im.entity.ChatMessage;
import com.preferredmall.im.entity.ChatSession;
import com.preferredmall.im.mapper.ChatMessageMapper;
import com.preferredmall.im.mapper.ChatSessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * IM 业务服务：会话列表 / 历史消息 / 发送 / 已读回执。长连接 Netty 作为可选增强。
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionMapper sessionMapper;
    private final ChatMessageMapper messageMapper;

    /**
     * 查找或创建会话：约定 userId 存较小 ID、contactId 存较大 ID
     */
    public ChatSession getOrCreateSession(Long uid, Long contactId) {
        long u = Math.min(uid, contactId);
        long c = Math.max(uid, contactId);
        ChatSession s = sessionMapper.selectOne(new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getUserId, u).eq(ChatSession::getContactId, c).last("LIMIT 1"));
        if (s == null) {
            s = new ChatSession();
            s.setUserId(u);
            s.setContactId(c);
            s.setUnreadCountA(0);
            s.setUnreadCountB(0);
            sessionMapper.insert(s);
        }
        return s;
    }

    public List<ChatSession> listSessions(Long uid) {
        return sessionMapper.selectList(new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getUserId, uid).or().eq(ChatSession::getContactId, uid)
                .orderByDesc(ChatSession::getLastMsgTime));
    }

    public List<ChatMessage> listHistory(Long sessionId, Long uid) {
        ChatSession session = sessionMapper.selectById(sessionId);
        checkParticipant(session, uid);
        return messageMapper.selectList(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getSessionId, sessionId)
                .orderByAsc(ChatMessage::getCreateTime));
    }

    @Transactional(rollbackFor = Exception.class)
    public ChatMessage send(Long fromId, Long toId, ChatMessage body) {
        ChatSession session = getOrCreateSession(fromId, toId);
        body.setSessionId(session.getId());
        body.setFromUserId(fromId);
        body.setToUserId(toId);
        body.setIsRead(0);
        messageMapper.insert(body);
        // 更新会话最后一条消息 + 未读数
        session.setLastMsgContent(body.getContent());
        session.setLastMsgTime(body.getCreateTime() == null ? java.time.LocalDateTime.now() : body.getCreateTime());
        boolean aIsSender = session.getUserId().equals(fromId);
        session.setUnreadCountA(session.getUnreadCountA() + (aIsSender ? 0 : 1));
        session.setUnreadCountB(session.getUnreadCountB() + (aIsSender ? 1 : 0));
        sessionMapper.updateById(session);
        return body;
    }

    public void markRead(Long sessionId, Long readerId) {
        ChatSession session = sessionMapper.selectById(sessionId);
        checkParticipant(session, readerId);
        List<ChatMessage> msgs = messageMapper.selectList(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getSessionId, sessionId)
                .eq(ChatMessage::getToUserId, readerId)
                .eq(ChatMessage::getIsRead, 0));
        if (msgs.isEmpty()) return;
        messageMapper.update(null, new LambdaUpdateWrapper<ChatMessage>()
                .eq(ChatMessage::getSessionId, sessionId)
                .eq(ChatMessage::getToUserId, readerId)
                .eq(ChatMessage::getIsRead, 0)
                .set(ChatMessage::getIsRead, 1));
        boolean aIsReader = session.getUserId().equals(readerId);
        session.setUnreadCountA(aIsReader ? 0 : session.getUnreadCountA());
        session.setUnreadCountB(aIsReader ? session.getUnreadCountB() : 0);
        sessionMapper.updateById(session);
    }

    /**
     * 越权防护：只有会话参与者才能查看/操作会话
     */
    private void checkParticipant(ChatSession session, Long uid) {
        if (session == null) throw new BusinessException(404, "会话不存在");
        if (!session.getUserId().equals(uid) && !session.getContactId().equals(uid)) {
            throw new BusinessException(403, "无权访问该会话");
        }
    }
}
