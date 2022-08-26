package com.njh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njh.common.R;
import com.njh.domain.OrderDetail;
import com.njh.domain.Orders;
import com.njh.dto.OrderStatus;
import com.njh.dto.OrdersDto;
import com.njh.service.OrderDetailService;
import com.njh.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("下单成功！");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number,String beginTime,String endTime){
        Page pageInfo = new Page<>(page,pageSize);
        return orderService.pageQuery(pageInfo,number,beginTime,endTime);
    }

    @PutMapping
    public R<String> status(@RequestBody OrdersDto ordersDto){
        orderService.updateStatusById(ordersDto);
        return R.success("修改订单状态成功！");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize) {
        Page pageInfo = new Page<>(page, pageSize);
        return orderService.userPage(pageInfo);
    }


    @DeleteMapping
    @Transactional(rollbackFor = Exception.class)
    public R<String> deleteOrder(Long id) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, id);
        orderDetailService.remove(queryWrapper);
        orderService.removeById(id);
        return R.success("删除成功！");
    }


    @PostMapping("/again")
    public R<String> addOrderAgain(@RequestBody Orders orders) {
        if (orders.getId() != null) {
            return R.success("成功！");
        }
        return R.error("失败!");
    }
}