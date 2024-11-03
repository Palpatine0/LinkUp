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

    private String identifier;

    /**
     * 0: admin;
     * 1: client;
     * 2: servant
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

    @TableField("phone_number")
    private String phoneNumber;

    private String nickname;

    private Integer age;

    /**
     * 0: male;
     * 1: female
     */
    private Integer gender;

    private String avatar;

    private String language;

    @TableField("completed_order_count")
    private Integer completedOrderCount;

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

    private BigDecimal balance;

    @TableField("looking_coins")
    private BigDecimal lookingCoins;

    @TableField("last_withdrawal_date")
    private Date lastWithdrawalDate;

    @TableField("is_identity_verified")
    private Integer isIdentityVerified;

    @TableField("id_card_number")
    private String idCardNumber;

    @TableField("id_card_birthday")
    private Date idCardBirthday;

    @TableField("id_card_name")
    private String idCardName;

    @TableField("id_card_gender")
    private Integer idCardGender;

    @TableField("id_card_address")
    private String idCardAddress;

    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("created_at")
    private Date createdAt;

    // Transient fields
    @TableField(exist = false)
    private BigDecimal quotedPrice;

    @TableField(exist = false)
    private Message latestMessage;

    @TableField(exist = false)
    private Integer unreadMessageCount;
}
