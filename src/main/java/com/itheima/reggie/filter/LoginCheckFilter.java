package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: zgq
 * Create: 2023/4/24 9:18
 * Description: 过滤器，判断是否允许请求
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    // 路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long id = Thread.currentThread().getId();
        log.info("线程id:{}", id);
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("当前请求:{}", requestURI);
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/user/login",
                "/user/sendMsg",
                "/backend/**",
                "/front/**",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        // 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        // 若不需要处理，则直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }
        // 判断登录状态，已登录，直接放行
        if (request.getSession().getAttribute("employee") != null) {
            // 基于ThreadLocal存储当前登录员工id
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            // 放行
            filterChain.doFilter(request, response);
            return;
        }

        // 判断移动端登录状态
        if (request.getSession().getAttribute("user") != null) {
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        // 未登录返回未登录结果，通过输出流的方式向客户端响应数据
        log.info("用户未登录");
        log.info("拦截到请求:{}", requestURI);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
