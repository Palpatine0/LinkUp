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

    @TableField("card_number")
    private String cardNumber;

    @TableField("card_type")
    private String cardType;

    @TableField("card_issuer")
    private String cardIssuer;

    @TableField("card_network")
    private String cardNetwork;

    /**
     * 0: not deleted;
     * 1: deleted
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
