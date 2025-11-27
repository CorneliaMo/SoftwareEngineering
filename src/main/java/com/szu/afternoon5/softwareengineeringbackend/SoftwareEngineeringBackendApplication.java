package com.szu.afternoon5.softwareengineeringbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SoftwareEngineeringBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoftwareEngineeringBackendApplication.class, args);
    }

}
