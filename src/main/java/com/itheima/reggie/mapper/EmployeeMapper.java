package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: zgq
 * Create: 2023/4/23 19:48
 * Description:
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
