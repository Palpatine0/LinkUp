package com.enchanted.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("bank")
public class Bank {

    private Long id;

    @TableField("name")
    private String name;

    @TableField("abbr")
    private String abbr;

    @TableField("logo")
    private String logo;

}
