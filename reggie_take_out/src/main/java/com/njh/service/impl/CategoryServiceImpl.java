package com.njh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.njh.common.CustomException;
import com.njh.dao.CategoryMapper;
import com.njh.domain.Category;
import com.njh.domain.Dish;
import com.njh.domain.Setmeal;
import com.njh.service.CategoryService;
import com.njh.service.DishService;
import com.njh.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    @Override
    public void remove(Long id) {
        //查询是否关联

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        if (count1 > 0){

            throw new CustomException("当前分类已关联菜品,不能删除");

        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper =new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if (count2 > 0){

            throw new CustomException("当前分类已关联套餐,不能删除");
        }

        super.removeById(id);

    }
}
