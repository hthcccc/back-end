package com.example.demo.error;

public class UserExistedError implements ServiceError{
    @Override
    public String getMessage(){return "User is existed!";}

    @Override
    public Integer getCode(){return -1;}
}
