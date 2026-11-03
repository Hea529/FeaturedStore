package com.preferredmall.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("banner")
public class Banner implements Serializable {
    @TableId(type = IdType.AUTO) private Long id;
    @TableField("image_url") private String imageUrl;
    @TableField("link_url") private String linkUrl;
    /** 1 商品 / 2 分类 / 3 外链 */
    @TableField("link_type") private Integer linkType;
    private Integer sort;
    /** 1 启用 / 0 禁用 */
    private Integer status;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT) private LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE) private LocalDateTime updateTime;
}
