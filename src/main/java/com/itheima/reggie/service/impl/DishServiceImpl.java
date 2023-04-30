package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: zgq
 * Create: 2023/4/25 8:49
 * Description:
 */
@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;


    // 添加菜品及其口味
    @Override
    public void saveDishWithFlavor(DishDto dishDto) {
        // 保存菜品到菜品表
        super.save(dishDto);
        Long id = dishDto.getId();

        // 为菜品口味设置菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        // 保存菜品口味到口味表
        dishFlavorService.saveBatch(flavors);
    }

    // 根据id获取菜品及其口味
    @Override
    public DishDto getDishWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        // 查询菜品
        Dish dish = super.getById(id);
        BeanUtils.copyProperties(dish, dishDto);
        // 查询口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavorList);

        return dishDto;
    }

    // 修改菜品及口味
    @Override
    public void updateDishWithFlavor(DishDto dishDto) {
        // 保存菜品到菜品表
        this.updateById(dishDto);

        // 根据菜品id清理原有口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // 保存口味到口味表
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
