package com.waerwak.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waerwak.entity.Dish;
import com.waerwak.mapper.DishMapper;
import com.waerwak.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl
        extends ServiceImpl<DishMapper, Dish>
        implements DishService {
}
