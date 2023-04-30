package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;

/**
 * Author: zgq
 * Create: 2023/4/25 8:52
 * Description:
 */
public interface SetmealService extends IService<Setmeal> {
    void saveSetmealWithDish(SetmealDto setmealDto);

    SetmealDto getSetmealWithDishById(Long id);

    void updateSetmealWithDish(SetmealDto setmealDto);
}
