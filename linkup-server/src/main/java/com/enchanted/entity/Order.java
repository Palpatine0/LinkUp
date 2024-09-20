package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("`order`")
public class Order {

    private Long id;

    private String title;

    @TableField("client_id")
    private Long clientId;

    @TableField("servant_id")
    private Long servantId;

    @TableField("required_servant_type")
    private String requiredServantType;

    @TableField("required_gender")
    private String requiredGender;

    @TableField("required_age_min")
    private Integer requiredAgeMin;

    @TableField("required_age_max")
    private Integer requiredAgeMax;

    @TableField("service_duration")
    private Integer serviceDuration;

    private String state;

    private String city;

    private String address;

    private BigDecimal price;

    private BigDecimal rating;

    @TableField("candidate_count")
    private Integer candidateCount;

    private Integer status; // 0: pending; 1: processing; 2: completed

    @TableField("payment_method")
    private String paymentMethod;

    @TableField("effective_at")
    private Date effectiveAt;

    @TableField("expire_at")
    private Date expireAt;

    @TableField("close_at")
    private Date closeAt;

    @TableField("completed_at")
    private Date completedAt;

    @TableField("is_completed")
    private Boolean isCompleted;

    @TableField("is_deleted")
    private Boolean isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
