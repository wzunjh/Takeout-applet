package com.njh.common;

public class BaseContext {

    //多线程访问同一个共享变量为了防止高并发，采用加锁同步方式

    private static ThreadLocal<Long> threadLocal =new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return  threadLocal.get();
    }
}
