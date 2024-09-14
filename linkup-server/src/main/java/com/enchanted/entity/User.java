package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("`user`")
public class User {

    private Long id;

    /**
     * Role: 0: admin; 1: client; 2: servant
     */

    private Integer role;

    @TableField("openid")
    private String openid;

    @TableField("session_key")
    private String sessionKey;

    @TableField("unionid")
    private String unionid;

    private String nickname;

    private Integer age;

    /**
     * Gender: 0: male; 1: female
     */
    private String gender;

    private String avatar;

    private String language;

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
