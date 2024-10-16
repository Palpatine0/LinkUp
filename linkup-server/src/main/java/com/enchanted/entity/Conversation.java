package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("conversation")
public class Conversation {

    private Long id;

    @TableField("client_id")
    private Long clientId;

    @TableField("servant_id")
    private Long servantId;

    @TableField("expiration_time")
    private Date expirationTime;

    @TableField("is_deleted")
    private Boolean isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
