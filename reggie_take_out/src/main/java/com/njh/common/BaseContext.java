package com.njh.common;

public class BaseContext {

    //封装一个用户访问令牌

    private static ThreadLocal<Long> threadLocal =new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return  threadLocal.get();
    }
}
