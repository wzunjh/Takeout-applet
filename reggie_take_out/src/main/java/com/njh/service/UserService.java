package com.njh.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.njh.common.R;
import com.njh.domain.User;

public interface UserService extends IService<User> {
    void sendMsg(String phone, String subject, String context);

    R<String> logout();
}
