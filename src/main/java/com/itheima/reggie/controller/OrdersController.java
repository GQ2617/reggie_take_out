package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Author: zgq
 * Create: 2023/4/27 11:55
 * Description:
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @GetMapping("/page")
    public R<Page> get(Integer page, Integer pageSize, Date beginTime, Date endTime) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        // queryWrapper.between(Orders::getOrderTime, beginTime, endTime);
        ordersService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("支付了");
    }

    @GetMapping("/userPage")
    public R<Page> get(Integer page, Integer pageSize) {
        Page<Orders> ordersPage = new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        ordersService.page(ordersPage, queryWrapper);
        return R.success(ordersPage);
    }

}
