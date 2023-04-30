package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: zgq
 * Create: 2023/4/25 21:05
 * Description:
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页条件查询所有菜品
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> get(Integer page, Integer pageSize, String name) {
        // 分页
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        // 条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(name), Dish::getName, name);
        queryWrapper.orderByAsc(Dish::getCreateTime);
        // 执行
        dishService.page(pageInfo, queryWrapper);

        // 处理dishDtoPage
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            // 将Dish对象转为DishDto对象
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            // 根据id查询菜品分类名称
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            // 添加分类名称
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    /**
     * 添加菜品及口味
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveDishWithFlavor(dishDto);
        return R.success("添加菜品及口味成功");
    }


    /**
     * 根据id获取菜品及口味
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getDishWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品及口味
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateDishWithFlavor(dishDto);
        return R.success("修改菜品信息成功");
    }

    /**
     * 删除及批量删除菜品和口味
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String ids) {
        // 删除菜品
        dishService.removeBatchByIds(Arrays.asList(ids.split(",")));
        // 删除菜品口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, Arrays.asList(ids.split(",")));
        dishFlavorService.remove(queryWrapper);
        return R.success("删除成功");
    }

    /**
     * 修改菜品状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> update(@PathVariable Integer status, String ids) {
        Dish dish = new Dish();
        dish.setStatus(status);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, Arrays.asList(ids.split(",")));
        dishService.update(dish, queryWrapper);
        return R.success("修改状态成功");
    }


    /**
     * 根据菜品分类获取菜品
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> get(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, dish.getStatus());
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }
}
