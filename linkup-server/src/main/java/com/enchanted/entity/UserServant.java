package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("`user_servant`")
public class UserServant {

    private Long id;

    @TableField("user_id")
    private Long userId;

    private String bio;

    @TableField("servant_type_id")
    private String serviceTypeId;

    @TableField("is_deleted")
    private Boolean isDeleted;

    @TableField("created_at")
    private Date createdAt;
}
