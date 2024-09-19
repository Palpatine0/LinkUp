package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("`service_type`")
public class ServiceType {

    private Long id;

    private String name;

    @TableField("cover_img")
    private String coverImg;

    @TableField("intro_img")
    private String introImg;

    @TableField("is_deleted")
    private Boolean isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
