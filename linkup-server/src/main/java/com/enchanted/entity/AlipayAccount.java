package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("alipay_account")
public class AlipayAccount {

    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("name")
    private String name;

    /**
     * 0: not deleted;
     * 1: deleted
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
