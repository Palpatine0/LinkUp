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

    private String openid;

    @TableField("session_key")
    private String sessionKey;

    private String unionid;

    @TableField("referrer_id")
    private String referrerId;

    @TableField("referral_qr_code")
    private String referralQrCode;

    @TableField("referral_code")
    private String referralCode;

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

    @TableField("location")
    private String location;

    @TableField("location_name")
    private String locationName;

    @TableField("city")
    private String city;

    @TableField("latitude_fuzzy")
    private Double latitudeFuzzy;

    @TableField("longitude_fuzzy")
    private Double longitudeFuzzy;

    @TableField("is_deleted")
    private Boolean isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
