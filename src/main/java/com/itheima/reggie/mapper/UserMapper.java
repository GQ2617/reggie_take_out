package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: zgq
 * Create: 2023/4/27 19:59
 * Description:
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
