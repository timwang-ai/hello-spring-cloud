package com.tim.hello.spring.cloud.web.admin.feign.service;

import com.tim.hello.spring.cloud.web.admin.feign.service.hystrix.AdminServiceHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ Author  : Tim Wang
 * @ FileName: AdminService.java
 * @ Time    : 2020/8/5 13:46
 */

// fallback = AdminServiceHystrix.class  增加熔断器
@FeignClient(value = "hello-spring-cloud-service-admin", fallback = AdminServiceHystrix.class)
public interface  AdminService {

    @RequestMapping(value = "hi", method = RequestMethod.GET)
    public String sayHi(@RequestParam(value = "message") String message);
}
