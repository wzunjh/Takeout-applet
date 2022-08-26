package com.njh.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njh.domain.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
