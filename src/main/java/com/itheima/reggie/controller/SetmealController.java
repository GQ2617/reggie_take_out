package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
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
 * Create: 2023/4/26 20:19
 * Description:
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询所有套餐
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> get(Integer page, Integer pageSize, String name) {
        // 分页
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDishPage = new Page<>();
        // 条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(name), Setmeal::getName, name);
        // 执行
        setmealService.page(pageInfo, queryWrapper);

        // 获取page
        // 拷贝page的分页信息至dtoPage
        // 处理page的records信息
        // 遍历records
        // 拷贝records每条记录至dto对象
        // 根据records每条记录中的外键获取dto中属性信息,添加至dto
        // List<dto>集合接收
        // dtoPage接收List<dto>

        // 整合
        BeanUtils.copyProperties(pageInfo, setmealDishPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            // 根据id查询套餐分类信息
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();

            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDishPage.setRecords(list);

        return R.success(setmealDishPage);
    }

    /**
     * 根据id查询套餐信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealWithDishById = setmealService.getSetmealWithDishById(id);
        return R.success(setmealWithDishById);
    }

    /**
     * 添加套餐及套餐菜品
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveSetmealWithDish(setmealDto);
        return R.success("添加套餐成功");
    }

    /**
     * 修改套餐及其菜品
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateSetmealWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String ids) throws CustomException {
        // 判断套餐是否可以删除
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        long count = setmealService.count(setmealLambdaQueryWrapper);
        if (count > 0) {
            throw new CustomException("套餐正在售卖,不能删除");
        }

        // 删除套餐
        setmealService.removeBatchByIds(Arrays.asList(ids.split(",")));
        // 删除菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, Arrays.asList(ids.split(",")));
        setmealDishService.remove(queryWrapper);
        return R.success("删除套餐成功");
    }

    /**
     * 修改套餐状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> update(@PathVariable Integer status, String ids) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, Arrays.asList(ids.split(",")));
        setmealService.update(setmeal, queryWrapper);
        return R.success("修改套餐状态成功");
    }

    /**
     * 根据套餐分类id获取套餐
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> get(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus, setmeal.getStatus());
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

}
