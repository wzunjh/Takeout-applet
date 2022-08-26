package com.njh;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest

class ReggieTakeOutApplicationTests {


    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void textString() {

        //��ͨ��ֵ��
        redisTemplate.opsForValue().set("n","123");

        String n = (String) redisTemplate.opsForValue().get("n");

        System.out.println(n);

        //10����Զ�ɾ��
        redisTemplate.opsForValue().set("key1","value1",10l, TimeUnit.SECONDS);


        //��key������ʱ�ḳֵ
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("city1234", "nanjing");

        System.out.println(aBoolean);

    }

    /**
     * ����Hash��������
     */
    @Test
    public void testHash(){
        HashOperations hashOperations = redisTemplate.opsForHash();

        //��ֵ
        hashOperations.put("002","name","xiaoming");
        hashOperations.put("002","age","20");
        hashOperations.put("002","address","bj");

        //ȡֵ
        String age = (String) hashOperations.get("002", "age");
        System.out.println(age);

        //���hash�ṹ�е������ֶ�
        Set keys = hashOperations.keys("002");
        for (Object key : keys) {
            System.out.println(key);
        }

        //���hash�ṹ�е�����ֵ
        List values = hashOperations.values("002");
        for (Object value : values) {
            System.out.println(value);
        }
    }


    /**
     * ����List���͵�����
     */
    @Test
    public void testList(){
        ListOperations listOperations = redisTemplate.opsForList();

        //��ֵ
        listOperations.leftPush("mylist","a");
        listOperations.leftPushAll("mylist","b","c","d");

        //ȡֵ
        List<String> mylist = listOperations.range("mylist", 0, -1);
        for (String value : mylist) {
            System.out.println(value);
        }

        //����б��� llen
        Long size = listOperations.size("mylist");
        int lSize = size.intValue();
        for (int i = 0; i < lSize; i++) {
            //������
            String element = (String) listOperations.rightPop("mylist");
            System.out.println(element);
        }
    }


    /**
     * ����Set���͵�����
     */
    @Test
    public void testSet(){
        SetOperations setOperations = redisTemplate.opsForSet();

        //��ֵ
        setOperations.add("myset","a","b","c","a");

        //ȡֵ
        Set<String> myset = setOperations.members("myset");
        for (String o : myset) {
            System.out.println(o);
        }

        //ɾ����Ա
        setOperations.remove("myset","a","b");

        //ȡֵ
        myset = setOperations.members("myset");
        for (String o : myset) {
            System.out.println(o);
        }

    }

    /**
     * ����ZSet���͵�����
     */
    @Test
    public void testZset(){
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        //��ֵ
        zSetOperations.add("myZset","a",10.0);
        zSetOperations.add("myZset","b",11.0);
        zSetOperations.add("myZset","c",12.0);
        zSetOperations.add("myZset","a",13.0);

        //ȡֵ
        Set<String> myZset = zSetOperations.range("myZset", 0, -1);
        for (String s : myZset) {
            System.out.println(s);
        }

        //�޸ķ���
        zSetOperations.incrementScore("myZset","b",20.0);

        //ȡֵ
        myZset = zSetOperations.range("myZset", 0, -1);
        for (String s : myZset) {
            System.out.println(s);
        }

        //ɾ����Ա
        zSetOperations.remove("myZset","a","b");

        //ȡֵ
        myZset = zSetOperations.range("myZset", 0, -1);
        for (String s : myZset) {
            System.out.println(s);
        }
    }

    /**
     * ͨ�ò�������Բ�ͬ���������Ͷ����Բ���
     */
    @Test
    public void testCommon(){
        //��ȡRedis�����е�key
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            System.out.println(key);
        }

        //�ж�ĳ��key�Ƿ����
        Boolean itcast = redisTemplate.hasKey("itcast");
        System.out.println(itcast);

        //ɾ��ָ��key
        redisTemplate.delete("myZset");

        //��ȡָ��key��Ӧ��value����������
        DataType dataType = redisTemplate.type("myset");
        System.out.println(dataType.name());

    }






}
