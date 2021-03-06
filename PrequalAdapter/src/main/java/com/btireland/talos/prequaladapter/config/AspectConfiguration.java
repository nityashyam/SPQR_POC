package com.btireland.talos.prequaladapter.config;

import com.btireland.talos.prequaladapter.aop.LoggingAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfiguration {

    @Bean
    @ConditionalOnProperty(name = "debug", matchIfMissing = false)
    public LoggingAspect loggingAspect(Environment env) {
        return new LoggingAspect(env);
    }
}
