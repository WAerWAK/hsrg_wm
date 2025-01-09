package com.waerwak.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waerwak.entity.Orders;
import com.waerwak.mapper.OrdersMapper;
import com.waerwak.service.OrdersService;
import org.springframework.stereotype.Service;

/**
 * @FileName OrdersServiceImpl
 * @Date 2025/1/8
 * @Description <
 */
@Service
public class OrdersServiceImpl
        extends ServiceImpl<OrdersMapper, Orders>
        implements OrdersService {
}
