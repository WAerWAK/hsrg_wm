package com.waerwak.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waerwak.common.R;
import com.waerwak.entity.Orders;
import com.waerwak.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @FileName OrdersController
 * @Date 2025/1/8
 * @Description 关于订单模块的接口
 */
@RestController
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 查询列表页接口
     */
    @GetMapping("/order/page")
    public R<Page<Orders>> getOrderDetailPage(
            @RequestParam(defaultValue = "1") Integer page,  // 默认第一页
            @RequestParam(defaultValue = "10") Integer pageSize,  // 默认每页10条记录
            Orders orders,  // 订单过滤条件（如订单号、状态等）
            @RequestParam(required = false) String number,  // 订单号
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,  // 开始时间
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime  // 结束时间
    ) {
        // 创建分页对象,确保page和pageSize为整数类型
        page = Integer.valueOf(page);
        pageSize = Integer.valueOf(pageSize);
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();

        // 如果传入了订单号，设置订单号模糊查询条件
        if (number != null && !number.trim().isEmpty()) {
            queryWrapper.like("number", "%" + number + "%");
        }

        if (beginTime != null && endTime != null) {
            // 设置 orderTime 在 beginTime 之后，并且 checkoutTime 在 endTime 之前
            queryWrapper.ge("order_time", beginTime)  // order_time >= beginTime
                    .le("checkout_time", endTime); // checkout_time <= endTime
        }

        // 调用 Service 层进行分页查询
        Page<Orders> pageResult = ordersService.page(pageInfo, queryWrapper);

        // 确保返回的分页数据中total为整数类型
        pageResult.setTotal(Long.valueOf(pageResult.getTotal()).longValue());

        // 返回成功响应，包含分页数据
        return R.success(pageResult);
    }

    /**
     * 更新订单详情
     */
    @PutMapping("/order")
    public R<Orders> editOrderDetail(@RequestBody Orders orders) {
        System.out.println("orders = " + orders.toString());
        boolean b = ordersService.updateById(orders);
        if (b){
            return R.success(orders);
        }
        return R.error("出现错误！！");
    }
}
