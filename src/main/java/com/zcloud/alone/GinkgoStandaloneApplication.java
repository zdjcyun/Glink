package com.zcloud.alone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.zcloud.ginkgo","com.zcloud.alone"})
@EnableFeignClients(basePackages = {"com.zcloud.alone.network.device.feign.client"})
@SpringBootApplication
public class GinkgoStandaloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(GinkgoStandaloneApplication.class, args);
    }

}
