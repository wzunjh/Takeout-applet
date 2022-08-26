package com.njh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.njh.common.R;
import com.njh.domain.Dish;
import com.njh.dto.DishDto;

import java.util.List;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息
    public void updateWithFlavor(DishDto dishDto);

    R<List<DishDto>> findDishByCategoryId(Dish dish);
}
