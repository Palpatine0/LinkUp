package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("withdrawal_request")
public class WithdrawalRequest {

    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("admin_id")
    private Long adminId;

    private BigDecimal amount;

    /**
     * 0: Alipay; 1: Bank Card
     */
    private Integer method;

    @TableField("method_id")
    private Long methodId;

    /**
     * 0: Pending; 1: Approved; 2: Rejected; 3: Completed; 4: Failed
     */
    private Integer status;

    @TableField("created_at")
    private Date createdAt;

    @TableField("updated_at")
    private Date updatedAt;

    /**
     * 0: Not deleted; 1: Deleted
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField(exist = false)
    private String icon;
}
