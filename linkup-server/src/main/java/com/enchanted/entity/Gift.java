package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("gift")
public class Gift {

    private Long id;

    @TableField("name")
    private String name;

    @TableField("chat_duration")
    private Integer chatDuration;

    @TableField("price")
    private BigDecimal price;

    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
