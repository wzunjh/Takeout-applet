package com.njh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.njh.dao.OrderDetailMapper;
import com.njh.domain.OrderDetail;
import com.njh.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}