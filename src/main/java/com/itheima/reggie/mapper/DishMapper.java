package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Author: zgq
 * Create: 2023/4/25 8:48
 * Description:
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
   
}
