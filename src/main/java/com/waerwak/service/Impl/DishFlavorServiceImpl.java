package com.waerwak.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waerwak.entity.DishFlavor;
import com.waerwak.mapper.DishFlavorMapper;
import com.waerwak.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @FileName DishFlavorServiceImpl
 * @Date 2025/1/8
 * @Description <
 */
@Service
public class DishFlavorServiceImpl
        extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
}
