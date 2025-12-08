package com.szu.afternoon5.softwareengineeringbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用入口，启动 Spring Boot 与定时任务支持。
 * <p>
 * 如需拓展启动逻辑，可在此处添加环境检查、预热缓存或注册监听器，确保应用启动前的准备工作就绪。
 */
@SpringBootApplication
@EnableScheduling
public class SoftwareEngineeringBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoftwareEngineeringBackendApplication.class, args);
    }

}
