package com.njh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.njh.common.R;
import com.njh.domain.ShoppingCart;


public interface ShoppingCartService extends IService<ShoppingCart> {

    R<ShoppingCart> add2shoppingCart(ShoppingCart shoppingCart);
}
