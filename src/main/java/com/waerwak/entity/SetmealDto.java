package com.waerwak.entity;

import lombok.Data;

import java.util.List;

/**
 * @FileName SetmealDto
 * @Date 2025/1/8
 * @Description <
 */
@Data
public class SetmealDto extends Setmeal {
    private String categoryName;
    private List<SetmealDish> setmealDishes;
}