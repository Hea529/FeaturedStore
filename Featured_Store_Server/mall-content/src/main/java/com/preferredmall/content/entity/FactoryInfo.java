package com.preferredmall.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("factory_info")
public class FactoryInfo implements Serializable {
    /** 通常只有一条 */
    @TableId(type = IdType.AUTO) private Long id;
    @TableField("factory_name") private String factoryName;
    private String description;
    /** 背景图 */
    @TableField("bg_image_url") private String bgImageUrl;
    /** 详情 HTML */
    private String content;
    @TableField("service_hotline") private String serviceHotline;
    @TableField("official_wechat") private String officialWechat;
    private String address;
    /** 版权文案 */
    @TableField("copyright_info") private String copyrightInfo;
}
