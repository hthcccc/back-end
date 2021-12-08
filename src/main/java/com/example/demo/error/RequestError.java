package com.example.demo.error;

public class RequestError implements ServiceError{
    @Override
    public String getMessage(){return "Request Error!";}

    @Override
    public Integer getCode(){return -1;}
}
