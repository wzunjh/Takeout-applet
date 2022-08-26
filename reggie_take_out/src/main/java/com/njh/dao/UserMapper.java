package com.njh.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.njh.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User>{
}
