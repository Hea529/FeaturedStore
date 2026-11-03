package com.preferredmall.im.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天消息：msg_type 1 文本 / 2 图片 / 3 商品卡片 / 4 语音
 */
@Data
@TableName("chat_message")
public class ChatMessage implements Serializable {
    @TableId(type = IdType.AUTO) private Long id;
    @TableField("session_id") private Long sessionId;
    @TableField("from_user_id") private Long fromUserId;
    @TableField("to_user_id") private Long toUserId;
    @TableField("msg_type") private Integer msgType;
    private String content;
    @TableField("product_id") private Long productId;
    @TableField("is_read") private Integer isRead;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT) private LocalDateTime createTime;
}
