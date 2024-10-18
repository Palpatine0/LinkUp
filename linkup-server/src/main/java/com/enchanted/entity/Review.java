package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("review")
public class Review {

    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("reviewer_id")
    private Long reviewerId;

    @TableField("reviewee_id")
    private Long revieweeId;

    private String comments;

    /**
     * 0: not deleted;
     * 1: deleted
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
