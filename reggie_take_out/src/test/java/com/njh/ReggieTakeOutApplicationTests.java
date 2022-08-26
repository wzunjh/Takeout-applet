package com.njh;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest

class ReggieTakeOutApplicationTests {


    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void textString() {

        //ÆÕÍ¨¼üÖµ¶Ô
        redisTemplate.opsForValue().set("n","123");


    }

}
