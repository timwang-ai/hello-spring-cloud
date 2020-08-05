package com.tim.hello.spring.cloud.web.admin.ribbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * @ Author  : Tim Wang
 * @ FileName: WebAdminRibbonApplication.java
 * @ Time    : 2020/8/5 12:09
 */

@SpringBootApplication
@EnableDiscoveryClient
// 使用熔断器防止服务雪崩
@EnableHystrix
// 使用熔断器仪表盘监控
@EnableHystrixDashboard
public class WebAdminRibbonApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebAdminRibbonApplication.class, args);
    }
}
