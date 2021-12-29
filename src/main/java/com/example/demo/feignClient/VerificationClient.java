package com.example.demo.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.service.VerificationService;

@FeignClient(value="sendVerification",url="https://api.netease.im")
public interface VerificationClient {
    @PostMapping("/sms/sendtemplate.action")
    String sendMessage(@RequestParam("address") String address);
}