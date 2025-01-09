package com.waerwak.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waerwak.entity.Setmeal;
import com.waerwak.mapper.SetmealMapper;
import com.waerwak.service.SetmealService;
import org.springframework.stereotype.Service;

/**
 * @FileName SetmealServiceImpl
 * @Date 2025/1/8
 * @Description <
 */
@Service
public class SetmealServiceImpl
        extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {
}
