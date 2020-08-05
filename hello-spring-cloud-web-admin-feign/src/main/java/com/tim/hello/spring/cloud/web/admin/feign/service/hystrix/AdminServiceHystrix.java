package com.tim.hello.spring.cloud.web.admin.feign.service.hystrix;

import com.tim.hello.spring.cloud.web.admin.feign.service.AdminService;
import org.springframework.stereotype.Component;

/**
 * @ Author  : Tim Wang
 * @ FileName: AdminServiceHystrix.java
 * @ Time    : 2020/8/5 14:39
 */
@Component
public class AdminServiceHystrix implements AdminService {

    @Override
    public String sayHi(String message) {
        return "Hi，your message is :\"" + message + "\" but request error.";
    }
}
