package com.preferredmall.im.controller;

import com.preferredmall.common.context.BaseContext;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.Result;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.im.entity.ChatMessage;
import com.preferredmall.im.entity.ChatSession;
import com.preferredmall.im.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * HTTP 接口层；长连接 Netty 可选增强
 */
@Tag(name = "聊天消息")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    private Long uid() {
        Long u = BaseContext.currentUserId();
        if (u == null) throw new IllegalStateException("未登录");
        return u;
    }

    @Operation(summary = "获取聊天会话列表")
    @GetMapping("/sessions")
    public Result<List<ChatSession>> sessions() {
        return Result.success(chatService.listSessions(uid()));
    }

    @Operation(summary = "获取聊天记录")
    @GetMapping("/history")
    public Result<List<ChatMessage>> history(@RequestParam Long contactId) {
        Long uid = uid();
        if (contactId.equals(uid)) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "不能与自己聊天");
        }
        ChatSession s = chatService.getOrCreateSession(uid, contactId);
        return Result.success(chatService.listHistory(s.getId(), uid));
    }

    @Operation(summary = "发送聊天消息")
    @PostMapping("/send")
    public Result<ChatMessage> send(@RequestParam Long toId, @RequestBody ChatMessage body) {
        return Result.success(chatService.send(uid(), toId, body));
    }

    @Operation(summary = "标记消息已读")
    @PostMapping("/read")
    public Result<Void> read(@RequestParam Long sessionId) {
        chatService.markRead(sessionId, uid());
        return Result.success();
    }
}
