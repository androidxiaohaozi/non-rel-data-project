package com.example.nonreldataproject;

import com.example.nonreldataproject.properties.OpcProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({OpcProperties.class})
@SpringBootApplication
public class NonRelDataProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(NonRelDataProjectApplication.class, args);
    }

}
