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

    @TableField("gift_id")
    private Long giftId;

    @TableField("expiration_time")
    private Date expirationTime;

    @TableField("last_client_message_time")
    private Date lastClientMessageTime;

    /**
     * 0: not required;
     * 1: required;
     */
    @TableField("servant_response_required")
    private Integer servantResponseRequired;

    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
