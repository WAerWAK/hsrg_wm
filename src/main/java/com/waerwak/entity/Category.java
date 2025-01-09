package com.waerwak.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @FileName Category
 * @Date 2025/1/7
 * @Description
 */
@Data
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer type;
    private String name;
    private Integer sort;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;
    private Long createUser;
    private Long updateUser;
}