package com.njh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.njh.common.R;
import com.njh.dao.CategoryMapper;
import com.njh.dao.DishFlavorMapper;
import com.njh.dao.DishMapper;
import com.njh.domain.Category;
import com.njh.domain.Dish;
import com.njh.domain.DishFlavor;
import com.njh.dto.DishDto;
import com.njh.service.DishFlavorService;
import com.njh.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate redisTemplate;



    @Transactional  //事务
    public void saveWithFlavor(DishDto dishDto) {

        this.save(dishDto);   //继承自dish，相关字段值先存入dish表

        Long dishId = dishDto.getId();   //获取出dishId

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item)->{

            item.setDishId(dishId);

            return  item;

        }).collect(Collectors.toList());  //将dishId一起放入表中

        dishFlavorService.saveBatch(flavors);

    }

    //id查询菜品信息与口味信息

    @Override
    public DishDto getByIdWithFlavor(Long id) {

        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());

        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {

        //先删除后添加
        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());

            return item;
        }).collect(Collectors.toList());


        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public R<List<DishDto>> findDishByCategoryId(Dish dish) {

        //redis缓存
        //构造key
        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();

        List<DishDto> dishDtoList =null;

        //从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList != null){
            return R.success(dishDtoList);
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishMapper.selectList(queryWrapper);
        dishDtoList = new ArrayList<>();

        for (Dish dish1 : dishes) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1,dishDto);
            Long categoryId = dish1.getCategoryId();
            Category category = categoryMapper.selectById(categoryId);
            if (category != null){
                String name = category.getName();
                dishDto.setCategoryName(name);
            }

            //当前菜品ID
            Long dish1Id = dish1.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dish1Id);
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);
            dishDtoList.add(dishDto);

        }

        //数据存入缓存
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);


        return R.success(dishDtoList);
    }
}
