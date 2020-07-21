package com.btireland.talos.mygroup.myproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfiguration {

    @Bean
    RestTemplate feignLoggerLevel() {
        return new RestTemplate();
    }

}
