package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("conversation")
public class Conversation {

    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("servant_id")
    private Long servantId;

    @TableField("end_time")
    private Date endTime;
}
