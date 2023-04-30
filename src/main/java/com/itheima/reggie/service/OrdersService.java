package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;
import org.springframework.stereotype.Service;

/**
 * Author: zgq
 * Create: 2023/4/27 11:53
 * Description:
 */
public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
