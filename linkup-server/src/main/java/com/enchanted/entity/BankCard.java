package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("bank_card")
public class BankCard {

    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("bank_id")
    private Long bankId;

    private String identifier;

    private String name;

    /**
     * 0: debit card
     * 1: credit card
     */
    private Integer type;

    /**
     * 0: not deleted;
     * 1: deleted
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("created_at")
    private Date createdAt;

    @TableField(exist = false)
    private Bank bank;
}
