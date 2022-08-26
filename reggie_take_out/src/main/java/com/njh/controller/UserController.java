package com.njh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.njh.common.R;
import com.njh.domain.User;
import com.njh.service.UserService;
import com.njh.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

        @Value("${spring.mail.username}")
        private String from;   // 邮件发送人

        @Autowired
        private JavaMailSender mailSender;

        @Autowired
        private UserService userService;

        // 发送邮箱验证码
        @PostMapping("/sendMsg") // sendMsg
        public R<String> sendMsg(@RequestBody User user, HttpSession session) {
            //  获取邮箱账号
            String phone = user.getPhone();

            String subject = "瑞吉外卖登录验证码";
            if (StringUtils.isNotEmpty(phone)) {
                String code = ValidateCodeUtils.generateValidateCode(6).toString();
                String context = "欢迎使用瑞吉外卖，您的登录验证码为: " + code + ",五分钟内有效，请妥善保管!";

                //保存验证码
                session.setAttribute(phone,code);

                // 真正地发送邮箱验证码
                userService.sendMsg(phone, subject, context);

                return R.success("验证码发送成功，请及时查看!");
            }
            return R.error("验证码发送失败，请重新输入!");
        }

    @PostMapping("/login")
    @Transactional
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        // 获取手机号
        String phone = (String) map.get("phone");
        // 获取验证码
        String code = (String) map.get("code");
        // session中获取验证码
        Object codeSession = session.getAttribute(phone);
        // 比对验证码
        if(codeSession!=null&&codeSession.equals(code)){
            // 成功，则登录
            // 判断当前用户是否为新用户，新用户自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            // 手机号查询新用户
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if(user==null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败,验证码有误");
    }


        @PostMapping("/loginout")
        public R<String> logout() {
            return userService.logout();
        }

}

