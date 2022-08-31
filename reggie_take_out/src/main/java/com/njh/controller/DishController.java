package com.njh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njh.common.R;
import com.njh.domain.Category;
import com.njh.domain.Dish;
import com.njh.domain.SetmealDish;
import com.njh.dto.DishDto;
import com.njh.service.CategoryService;
import com.njh.service.DishFlavorService;
import com.njh.service.DishService;
import com.njh.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private RedisTemplate redisTemplate;



    //新增菜品

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){

        dishService.saveWithFlavor(dishDto);

        //清理修改的菜品缓存
        String key = "dish_"+dishDto.getCategoryId()+"_1";

        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }


    //分页查询

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        Page<Dish> pageInfo = new Page<>(page,pageSize);

        Page<DishDto> dishDtoPage =new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null,Dish::getName,name);

        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,queryWrapper);

        //拷贝属性,不烤records属性
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if (category != null){

                String categoryName = category.getName();

                dishDto.setCategoryName(categoryName);
            }

            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);

    }

    //根据id查询菜品信息
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }


    //修改菜品
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        //清理修改的菜品缓存
        String key = "dish_"+dishDto.getCategoryId()+"_1";

        redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }


    //添加套餐时菜单回显
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        return dishService.findDishByCategoryId(dish);
    }

    //批量启售和停售
    @PostMapping("/status/{status}")
    public R<String> updateSaleStatus(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        //  菜品具体的售卖状态 由前端修改并返回，该方法传入的status是 修改之后的售卖状态，可以直接根据一个或多个菜品id进行查询并修改售卖即可
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Dish::getId,ids);

        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(ids != null,SetmealDish::getDishId,ids);


        List<Dish> list = dishService.list(queryWrapper);
        List<SetmealDish> list1 = setmealDishService.list(queryWrapper1);
        if (list1.size() != 0){
            return R.error("含有已绑定套餐菜品,售卖状态不可更改");
        }
        else if (list != null){
            for (Dish dish : list) {
                dish.setStatus(status);
                dishService.updateById(dish);
            }
            // 删除缓存
            dishService.listByIds(ids).stream().forEach(item -> {
                String key = "dish_"+item.getCategoryId()+"_1";
                redisTemplate.delete(key);
            });
            return R.success("菜品的售卖状态已更改！");
        }

        return R.error("服务器异常,售卖状态更改失败");

    }

    //删除
    @DeleteMapping
    public R<String> Delete(@RequestParam("ids") List<Long> ids){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Dish::getId,ids);

        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(ids != null,SetmealDish::getDishId,ids);


        List<Dish> list = dishService.list(queryWrapper);
        List<SetmealDish> list1 = setmealDishService.list(queryWrapper1);

        if (list1.size() != 0){
            return R.error("含有已绑定套餐菜品,菜品不可删除");
        }
        else if (list != null){
            // 删除缓存
            dishService.listByIds(ids).stream().forEach(item -> {
                String key = "dish_"+item.getCategoryId()+"_1";
                redisTemplate.delete(key);
            });
            for (Dish dish : list) {
                dishService.removeById(dish);
            }
            return R.success("成功删除菜品！");
        }

        return R.error("服务器异常,菜品删除失败");
    }




}
