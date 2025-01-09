package com.waerwak.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waerwak.entity.SetmealDish;
import com.waerwak.mapper.SetmealDishMapper;
import com.waerwak.mapper.SetmealMapper;
import com.waerwak.service.SetmealDishService;
import org.springframework.stereotype.Service;

/**
 * @FileName SetmealDishServiceImpl
 * @Date 2025/1/8
 * @Description <
 */
@Service
public class SetmealDishServiceImpl
        extends ServiceImpl<SetmealDishMapper, SetmealDish>
        implements SetmealDishService {
}
