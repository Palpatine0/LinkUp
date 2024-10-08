package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("message")
public class Message {

    private Long id;

    @TableField("sender_id")
    private Long senderId;

    @TableField("recipient_id")
    private Long recipientId;

    private String content;

    /**
     * 0: text;
     * 1: image;
     * 2: audio;
     * 3: video;
     * 4: file
     */
    @TableField("media_type")
    private Integer mediaType;

    @TableField("media_url")
    private String mediaUrl;

    /**
     * 0: sending;
     * 1: sent;
     * 2: send failed;
     */
    private Integer status;

    /**
     * 0: unread;
     * 1: read;
     */
    @TableField("is_read")
    private Integer isRead;

    @TableField("created_at")
    private Date createdAt;
}
