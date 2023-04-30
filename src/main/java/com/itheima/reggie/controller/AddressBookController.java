package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author: zgq
 * Create: 2023/4/27 19:47
 * Description:
 */
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 获取指定用户地址
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> get() {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 添加地址
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("添加地址成功");
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> updateAddress(@RequestBody AddressBook addressBook) {
        // 取消其他默认地址
        AddressBook addressBook1 = new AddressBook();
        addressBook1.setIsDefault(0);
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        addressBookService.update(addressBook1, queryWrapper);

        // 设置默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("设为默认地址成功");
    }

    /**
     * 获取默认地址信息
     *
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }


    /**
     * 根据id获取地址信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 修改用户地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success("修改用户地址成功");
    }

    /**
     * 根据id删除地址
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String ids) {
        addressBookService.removeById(ids);
        return R.success("删除地址成功");
    }

}
