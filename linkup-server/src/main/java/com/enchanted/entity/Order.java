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

    private String identifier;

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

    @TableField("service_schedule_start")
    private Date serviceScheduleStart;

    @TableField("service_schedule_end")
    private Date serviceScheduleEnd;

    private String location;

    @TableField("location_name")
    private String locationName;

    private String state;

    private String city;

    private Double latitude;

    private Double longitude;


    private BigDecimal price;

    /**
     * 0: limited;
     * 1: fair;
     * 2: good
     */
    private BigDecimal rating;

    @TableField("candidate_count")
    private Integer candidateCount;

    /**
     * 0: pending: waiting for servants to respond or reviewing servants
     * 1: processing: order is being processed
     * 2: completed: order is done
     * 3: canceled: order canceled because no servant is chosen in the designated time
     */
    private Integer status;

    @TableField("payment_method")
    private String paymentMethod;

    @TableField("countdown_start_at")
    private Date countdownStartAt;

    @TableField("completed_at")
    private Date completedAt;

    @TableField("is_completed")
    private Boolean isCompleted;

    @TableField("is_deleted")
    private Boolean isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
