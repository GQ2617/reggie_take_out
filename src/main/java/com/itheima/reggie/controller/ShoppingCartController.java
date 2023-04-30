package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author: zgq
 * Create: 2023/4/27 21:11
 * Description:
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 购物车列表
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> get() {
        List<ShoppingCart> list = shoppingCartService.list();
        return R.success(list);
    }

    /**
     * 加入菜品至购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<String> save(@RequestBody ShoppingCart shoppingCart) {
        // 设置用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart info = shoppingCartService.getOne(queryWrapper);
        if (info != null) {
            int number = info.getNumber();
            number++;
            info.setNumber(number);
            shoppingCartService.updateById(info);
        } else {
            shoppingCartService.save(shoppingCart);
        }

        return R.success("加入购物车成功");
    }

    /**
     * 删除购物车菜品
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart info = shoppingCartService.getOne(queryWrapper);
        if (info.getNumber() != 1) {
            int number = info.getNumber();
            number--;
            info.setNumber(number);
            shoppingCartService.updateById(info);
        } else {
            shoppingCartService.remove(queryWrapper);
        }
        return R.success("移出购物车成功");
    }


    /**
     * 清空购物车商品
     *
     * @return
     */
    @DeleteMapping("clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }

}
