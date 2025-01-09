package com.waerwak.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @FileName Orders
 * @Date 2025/1/8
 * @Description <
 */
@Data
@TableName("Orders")
public class Orders {
    @TableId(type = IdType.AUTO)
    private Long id; // 主键
    private String number; // 订单号
    private Integer status; // 订单状态：1待付款，2待派送，3已派送，4已完成，5已取消
    private Long userId; // 下单用户ID
    private Long addressBookId; // 地址ID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date orderTime; // 下单时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkoutTime; // 结账时间
    private Integer payMethod; // 支付方式：1微信，2支付宝
    private BigDecimal amount; // 实收金额
    private String remark; // 备注
    private String phone; // 联系电话
    private String address; // 地址
    private String userName; // 用户名称
    private String consignee; // 收货人
}
