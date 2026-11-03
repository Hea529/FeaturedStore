package com.preferredmall.im.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话：user_id 存较小 ID、contact_id 存较大 ID，保证一对用户只有一条会话
 */
@Data
@TableName("chat_session")
public class ChatSession implements Serializable {
    @TableId(type = IdType.AUTO) private Long id;
    @TableField("user_id") private Long userId;
    @TableField("contact_id") private Long contactId;
    @TableField("last_msg_content") private String lastMsgContent;
    @TableField("last_msg_time") private LocalDateTime lastMsgTime;
    @TableField("unread_count_a") private Integer unreadCountA;
    @TableField("unread_count_b") private Integer unreadCountB;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT) private LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE) private LocalDateTime updateTime;
}
