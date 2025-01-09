package com.waerwak.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @FileName DishFlavor
 * @Date 2025/1/8
 * @Description <
 */
@Data
public class DishFlavor {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long dishId;
    private String name;
    private String value;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;
    private Long createUser;
    private Long updateUser;
    private Integer isDeleted;
}
