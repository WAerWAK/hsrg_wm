package com.waerwak.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waerwak.entity.Category;
import com.waerwak.mapper.CategoryMapper;
import com.waerwak.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl
        extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {
}