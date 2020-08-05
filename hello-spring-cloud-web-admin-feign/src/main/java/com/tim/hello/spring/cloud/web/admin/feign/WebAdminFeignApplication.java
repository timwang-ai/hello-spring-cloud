package com.tim.hello.spring.cloud.web.admin.feign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ Author  : Tim Wang
 * @ FileName: WebAdminFeignApplication.java
 * @ Time    : 2020/8/5 13:45
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class WebAdminFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebAdminFeignApplication.class, args);
    }
}
