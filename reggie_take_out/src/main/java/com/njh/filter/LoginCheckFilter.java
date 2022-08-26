package com.njh.filter;

import com.alibaba.fastjson.JSON;
import com.njh.common.BaseContext;
import com.njh.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response =(HttpServletResponse) servletResponse;


        String requestURI = request.getRequestURI();


        //拦截到请求
        log.info("拦截到请求："+requestURI);

        //放行的路径
        String[] urls =new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        boolean check = check(urls,requestURI);


        if (check){
            filterChain.doFilter(request,response);    //放行
            return;
        }

        if (request.getSession().getAttribute("employee")!=null){

            Long empId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);    //获取当前id

            filterChain.doFilter(request,response);    //放行
            return;
        }


        Long userId = (Long) request.getSession().getAttribute("user");
        if (userId != null){

            // 自定义元数据对象处理器 MyMetaObjectHandler中需要使用 登录用户id
            //   通过ThreadLocal set和get用户id
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


    }

    public boolean check(String[] urls,String requestURI){
        for (String url : urls){
            boolean match = PATH_MATCHER.match(url,requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }

}
