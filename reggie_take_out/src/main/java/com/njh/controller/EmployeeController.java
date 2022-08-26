package com.njh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njh.common.R;
import com.njh.domain.Employee;
import com.njh.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());   //解密

        LambdaQueryWrapper<Employee> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());    //数据库用户名和返回的用户名比对
        Employee emp =employeeService.getOne(queryWrapper);     //获取整个对应实体类

        if(emp == null){
            return R.error("用户名或密码错误");
        }

        if (!emp.getPassword().equals(password)){
            return  R.error("用户名或密码错误");
        }

        if (emp.getStatus()==0){
            return R.error("此账号已禁用");
        }

        request.getSession().setAttribute("employee",emp.getId());    //将员工id存入Session


        return R.success(emp);
    }

    //退出功能
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");   //id移除
        return R.success("退出成功");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){

        //初始化密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));    //md5加密处理

//        //获取当前时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//        //获取当前操作者id
//        Long empId =(Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);


        employeeService.save(employee);


        return R.success("新增员工成功");

    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        Page pageInfo = new Page(page,pageSize);

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }


    //启用禁用员工信息
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    //编辑回显数据
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee =employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }

        return R.error("无法查询到相关信息");
    }


}
