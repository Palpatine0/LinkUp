package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("order_candidate")
public class OrderCandidate {

    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("servant_id")
    private Long servantId;

    @TableField("is_closed")
    private Boolean isClosed;

    @TableField("is_deleted")
    private Boolean isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
