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

    /**
     * 0: balance;
     * 1: looking coin;
     */
    @TableField("currency_type")
    private Integer currencyType;

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

    @TableField("description")
    private String description;

    @TableField("description_cn")
    private String descriptionCn;

    @TableField("created_at")
    private Date createdAt;

    // Transient fields
    @TableField(exist = false)
    private String paymentMethod;
}
