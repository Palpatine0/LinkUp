package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("`transaction`")
public class Transaction {

    private Long id;

    private String identifier;

    @TableField("user_id")
    private Long userId;

    @TableField("order_id")
    private Long orderId;

    private BigDecimal amount;

    @TableField("balance_after")
    private BigDecimal balanceAfter;

    /**
     * 0: deduction;
     * 1: addition
     */
    @TableField("transaction_type")
    private Integer transactionType;

    private String description;

    @TableField("created_at")
    private Date createdAt;
}
