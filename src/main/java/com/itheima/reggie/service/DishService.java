package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

/**
 * Author: zgq
 * Create: 2023/4/25 8:49
 * Description:
 */
public interface DishService extends IService<Dish> {
    // 添加菜品及其口味
    void saveDishWithFlavor(DishDto dishDto);

    // 根据id获取菜品及其口味
    DishDto getDishWithFlavor(Long id);

    // 修改菜品及其口味
    void updateDishWithFlavor(DishDto dishDto);
}
