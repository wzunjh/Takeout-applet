package com.njh.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.njh.common.R;
import com.njh.domain.Orders;
import com.njh.dto.OrdersDto;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);

    R<Page> userPage(Page pageInfo);

    R<Page> pageQuery(Page pageInfo, String number, String beginTime, String endTime);

    void updateStatusById(OrdersDto ordersDto);
}
