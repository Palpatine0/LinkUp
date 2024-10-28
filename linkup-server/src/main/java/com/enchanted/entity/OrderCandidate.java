package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("`order_candidate`")
public class OrderCandidate {

    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("servant_id")
    private Long servantId;

    @TableField("quoted_price")
    private BigDecimal quotedPrice;

    @TableField("quoted_price_updated_at")
    private Date quotedPriceUpdatedAt;

    @TableField("is_closed")
    private Boolean isClosed;

    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
