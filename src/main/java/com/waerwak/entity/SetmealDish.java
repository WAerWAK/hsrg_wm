package com.waerwak.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @FileName SetmealDish
 * @Date 2025/1/8
 * @Description <
 */
@Data
public class SetmealDish {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long setmealId;
    private Long dishId;
    private String name;
    private int price;
    private String copies;
    private String sort;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;
    private Long createUser;
    private Long updateUser;
    private Integer isDeleted;
}
