package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("order")
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

    @TableField("required_age_from")
    private Integer requiredAgeFrom;

    @TableField("required_age_to")
    private Integer requiredAgeTo;

    @TableField("service_duration")
    private Integer serviceDuration;

    private String city;

    private String address;

    private BigDecimal price;

    private BigDecimal rating;

    @TableField("candidate_count")
    private Integer candidateCount;

    @TableField("done_status")
    private Integer doneStatus;

    @TableField("processing_status")
    private Integer processingStatus;

    @TableField("effective_at")
    private Date effectiveAt;

    @TableField("expire_at")
    private Date expireAt;

    @TableField("completed_at")
    private Date completedAt;

    @TableField("created_at")
    private Date createdAt;

    @TableField("is_deleted")
    private Boolean isDeleted;
}
