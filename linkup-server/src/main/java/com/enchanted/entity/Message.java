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

    private String content; // Text content

    @TableField("media_type")
    private Integer mediaType; // 0: text, 1: image, 2: audio, 3: video, 4: file

    @TableField("media_url")
    private String mediaUrl; // URL or path to the media file

    private Integer status; // 0: sending; 1: sent

    @TableField("is_read")
    private Boolean isRead; // false: unread; true: read

    @TableField("created_at")
    private Date createdAt;
}
