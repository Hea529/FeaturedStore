package com.preferredmall.content.controller;

import com.preferredmall.common.context.BaseContext;
import com.preferredmall.common.result.Result;
import com.preferredmall.content.entity.*;
import com.preferredmall.content.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** C 端：读取 */
@Tag(name = "内容资讯")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @Operation(summary = "获取轮播图列表")
    @GetMapping("/banner/list")
    public Result<List<Banner>> banners() { return Result.success(contentService.listActiveBanners()); }

    @Operation(summary = "获取公告列表")
    @GetMapping("/notice/list")
    public Result<List<Notice>> notices() { return Result.success(contentService.listActiveNotices()); }

    @Operation(summary = "获取常见问题列表")
    @GetMapping("/faq/list")
    public Result<List<Faq>> faqs() { return Result.success(contentService.listActiveFaqs()); }

    @Operation(summary = "获取关于我们信息")
    @GetMapping("/about")
    public Result<FactoryInfo> about() { return Result.success(contentService.getFactory()); }

    @Operation(summary = "提交意见反馈")
    @PostMapping("/feedback")
    public Result<Feedback> feedback(@RequestBody Feedback fb) {
        Long uid = BaseContext.currentUserId();
        return Result.success(contentService.saveFeedback(uid, fb.getContent(), fb.getImageUrls(), fb.getContact()));
    }
}
