package com.njh.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njh.domain.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
