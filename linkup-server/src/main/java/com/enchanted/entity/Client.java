package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("client")
public class Client {

    private Long id;

    private String nickname;

    private Integer age;

    private String gender;

    private String avatar;

    private String openid;

    @TableField("session_key")
    private String sessionKey;

    private String unionid;

    @TableField("completed_order_count")
    private Integer completedOrderCount;

    private BigDecimal balance;

    @TableField("current_location")
    private String currentLocation;

    @TableField("is_deleted")
    private Boolean isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
