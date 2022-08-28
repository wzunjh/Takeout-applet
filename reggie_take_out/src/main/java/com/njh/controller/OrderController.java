package com.njh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njh.common.BaseContext;
import com.njh.common.R;
import com.njh.domain.OrderDetail;
import com.njh.domain.Orders;
import com.njh.domain.ShoppingCart;
import com.njh.dto.OrderStatus;
import com.njh.dto.OrdersDto;
import com.njh.service.OrderDetailService;
import com.njh.service.OrderService;
import com.njh.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("�µ��ɹ���");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number,String beginTime,String endTime){
        Page pageInfo = new Page<>(page,pageSize);
        return orderService.pageQuery(pageInfo,number,beginTime,endTime);
    }

    @PutMapping
    public R<String> status(@RequestBody OrdersDto ordersDto){
        orderService.updateStatusById(ordersDto);
        return R.success("�޸Ķ���״̬�ɹ���");
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
        return R.success("ɾ���ɹ���");
    }


    @PostMapping("/again")
    public R<String> againSubmit(@RequestBody Map<String,String> map){
        String ids = map.get("id");

        long id = Long.parseLong(ids);

        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        //��ȡ�ö�����Ӧ�����еĶ�����ϸ��
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);

        //ͨ���û�id��ԭ���Ĺ��ﳵ����գ������clean��������Ƶ�н�����,�����ȡ��service��,��ô����Ϳ���ֱ�ӵ�����
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(queryWrapper1);

        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item) -> {
            //�Ѵ�order���к�order_details���л�ȡ�������ݸ�ֵ��������ﳵ����
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setImage(item.getImage());
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();
            if (dishId != null) {
                //����ǲ�Ʒ�Ǿ���Ӳ�Ʒ�Ĳ�ѯ����
                shoppingCart.setDishId(dishId);
            } else {
                //��ӵ����ﳵ�����ײ�
                shoppingCart.setSetmealId(setmealId);
            }
            shoppingCart.setName(item.getName());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        //��Я�����ݵĹ��ﳵ�������빺�ﳵ��  �����������ķ���Ҫʹ������������
        shoppingCartService.saveBatch(shoppingCartList);

        return R.success("�����ɹ�");
    }
}