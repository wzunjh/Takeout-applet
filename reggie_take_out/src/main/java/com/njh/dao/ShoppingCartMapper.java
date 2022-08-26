package com.njh.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.njh.domain.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

}
