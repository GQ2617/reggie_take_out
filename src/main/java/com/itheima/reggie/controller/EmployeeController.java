package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.time.LocalDateTime;

/**
 * Author: zgq
 * Create: 2023/4/23 19:51
 * Description:
 */

@Slf4j
@RestController
@RequestMapping("/employee")
@Api(tags = "员工相关接口")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 1. 对密码进行加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. 查询数据库判断用户是否存在
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3. 用户不存在返回登录失败结果
        if (emp == null) {
            return R.error("登录失败，帐号不存在");
        }

        // 4. 用户存在，比对密码，不一致返回登陆失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败，密码错误");
        }

        // 5. 校验用户状态
        if (emp.getStatus() == 0) {
            return R.error("登录失败，用户已被禁用");
        }

        // 6. 登录成功，将员工id存储Session并返回登录成功
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出 清空session中id
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    @ApiOperation("添加员工")
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("员工信息,{}", employee.toString());

        // 设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // 设置创建更新时间，创建更新人
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());
        // employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
        // employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        boolean save = employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 查询员工（分页+模糊查询+修改时间排序）
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("获取所有员工")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
            @ApiImplicitParam(name = "name", value = "姓名", required = false)
    })
    public R<Page> page(Integer page, Integer pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        // 分页
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        // 条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 过滤条件
        queryWrapper.like(Strings.isNotEmpty(name), Employee::getName, name);
        // 排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     *
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    @ApiOperation("修改员工")
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        // 设置更新时间和修改人
        // employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        // employee.setUpdateTime(LocalDateTime.now());
        long id = Thread.currentThread().getId();
        log.info("线程id:{}", id);
        // 执行操作
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id获取员工")
    public R<Employee> get(@PathVariable String id) {
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
