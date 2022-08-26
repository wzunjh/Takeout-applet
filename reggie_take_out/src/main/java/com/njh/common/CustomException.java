package com.njh.common;


//自定义异常，在全局异常中捕获

public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
