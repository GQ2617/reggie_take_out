package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * Author: zgq
 * Create: 2023/4/24 21:44
 * Description:
 */
public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
