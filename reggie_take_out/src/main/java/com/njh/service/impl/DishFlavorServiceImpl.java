package com.njh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.njh.dao.DishFlavorMapper;
import com.njh.domain.DishFlavor;
import com.njh.service.DishFlavorService;
import com.njh.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
