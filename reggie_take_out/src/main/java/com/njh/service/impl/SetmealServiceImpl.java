package com.njh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.njh.common.CustomException;
import com.njh.common.R;
import com.njh.dao.SetmealMapper;
import com.njh.domain.DishFlavor;
import com.njh.domain.Setmeal;
import com.njh.domain.SetmealDish;
import com.njh.dto.SetmealDto;
import com.njh.service.SetmealDishService;
import com.njh.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private  SetmealService setmealService;

    @Autowired
    private SetmealMapper setmealMapper;



    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {

        this.save(setmealDto);

        List<SetmealDish> setmealDishes =setmealDto.getSetmealDishes();

        setmealDishes.stream().map((item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        })).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);  //判断是否在售

        int count = this.count(queryWrapper);

        if (count > 0){
            throw new CustomException("套餐正在售卖中,不能删除");
        }

        this.removeByIds(ids);   //删除setmeal表中数据

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(lambdaQueryWrapper);

    }

    @Override
    public SetmealDto getSetmealData(Long id) {
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(id != null,SetmealDish::getSetmealId,id);

        if (setmeal != null){
            BeanUtils.copyProperties(setmeal,setmealDto);

            List<SetmealDish> dishes = setmealDishService.list(queryWrapper);

            setmealDto.setSetmealDishes(dishes);

            return setmealDto;
        }

        return null;

    }

    @Override
    @Transactional
    public void updateWithdishById(SetmealDto setmealDto) {
        //先删除后添加
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());

        setmealDishService.remove(queryWrapper);

        List<SetmealDish> flavors = setmealDto.getSetmealDishes();

        flavors = flavors.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());

            return item;
        }).collect(Collectors.toList());


        setmealDishService.saveBatch(flavors);

    }

    @Override
    public R<List<Setmeal>> findSetmealByCategoryId(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealMapper.selectList(queryWrapper);
        return R.success(setmeals);
    }

}


