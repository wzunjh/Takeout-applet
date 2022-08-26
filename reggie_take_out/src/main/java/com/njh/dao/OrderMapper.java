package com.njh.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.njh.domain.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}