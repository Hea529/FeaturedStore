package com.preferredmall.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/** FAQ 原表有结构无代码，此处补齐 */
@Data
@TableName("faq")
public class Faq implements Serializable {
    @TableId(type = IdType.AUTO) private Long id;
    private String question;
    private String answer;
    private String category;
    private Integer sort;
    private Integer status;
}
