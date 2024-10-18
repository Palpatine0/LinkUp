package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("address")
public class Address {

    private Long id;

    @TableField("user_id")
    private Long userId;

    private String consignee;

    @TableField("phone_number")
    private String phoneNumber;

    private String address;

    @TableField("address_name")
    private String addressName;

    private String detail;

    private Double latitude;

    private Double longitude;

    /**
     * 0: not deleted;
     * 1: deleted
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
