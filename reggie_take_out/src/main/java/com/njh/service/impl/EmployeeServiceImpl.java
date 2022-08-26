package com.njh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.njh.dao.EmployeeMapper;
import com.njh.domain.Employee;
import com.njh.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
