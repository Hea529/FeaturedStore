package com.preferredmall.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.preferredmall.content.entity.*;
import com.preferredmall.content.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内容聚合服务：对外以模块为单位提供查询入口。后台管理端直接注入 Mapper 做 CRUD。
 */
@Service
@RequiredArgsConstructor
public class ContentService {

    private final BannerMapper bannerMapper;
    private final NoticeMapper noticeMapper;
    private final FactoryInfoMapper factoryMapper;
    private final FaqMapper faqMapper;
    private final FeedbackMapper feedbackMapper;

    public List<Banner> listActiveBanners() {
        return bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, 1).orderByAsc(Banner::getSort));
    }

    public List<Notice> listActiveNotices() {
        return noticeMapper.selectList(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getStatus, 1).orderByDesc(Notice::getCreateTime));
    }

    public FactoryInfo getFactory() {
        return factoryMapper.selectOne(new LambdaQueryWrapper<FactoryInfo>()
                .last("LIMIT 1"));
    }

    public List<Faq> listActiveFaqs() {
        return faqMapper.selectList(new LambdaQueryWrapper<Faq>()
                .eq(Faq::getStatus, 1).orderByAsc(Faq::getSort));
    }

    public Feedback saveFeedback(Long userId, String content, String imageUrls, String contact) {
        Feedback fb = new Feedback();
        fb.setUserId(userId);
        fb.setContent(content);
        fb.setImageUrls(imageUrls);
        fb.setContact(contact);
        fb.setStatus(0);
        feedbackMapper.insert(fb);
        return fb;
    }
}
