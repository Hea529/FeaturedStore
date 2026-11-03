package com.preferredmall.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback implements Serializable {
    @TableId(type = IdType.AUTO) private Long id;
    @TableField("user_id") private Long userId;
    private String content;
    /** 图片 URL，逗号分隔 */
    @TableField("image_urls") private String imageUrls;
    private String contact;
    /** 0 未处理 / 1 已回复 / 2 已处理 */
    private Integer status;
    @TableField("reply_content") private String replyContent;
    @TableField("reply_admin_id") private Long replyAdminId;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT) private LocalDateTime createTime;
    private LocalDateTime replyTime;
}
