package com.njh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.njh.common.R;
import com.njh.dao.UserMapper;
import com.njh.domain.User;
import com.njh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Value("${spring.mail.username}")
    private String from;   // 邮件发送人

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private HttpServletRequest request;

    @Override
    public void sendMsg(String phone, String subject, String context) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(from);
        mailMessage.setTo(phone);
        mailMessage.setSubject(subject);
        mailMessage.setText(context);

        // 真正的发送邮件操作，从 from到 to
        mailSender.send(mailMessage);

    }

    @Override
    public R<String> logout() {
        Long userId = (Long)request.getSession().getAttribute("user");
        request.getSession().removeAttribute("user");
        return R.success("退出成功！");
    }
}
