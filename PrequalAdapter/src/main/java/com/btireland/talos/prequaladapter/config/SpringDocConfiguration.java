package com.btireland.talos.prequaladapter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.api-version}") String apiVersion){
        return new OpenAPI()
                .info(new Info()
                        .title("Example REST API")
                        .version(apiVersion)
                        .description("Example REST API - provide services regarding example order domain"));
    }
}
