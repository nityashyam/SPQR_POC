package com.btireland.talos.mygroup.myproject.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties specific to this application.
 * <p>
 * Properties are configured in the {@code application*.yml} file.
 */
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationConfiguration {
    @Getter
    private final Example example = new Example();

    @Getter
    @Setter
    public static class Example {

        @Getter
        private final Asset asset = new Asset();

        @Getter
        @Setter
        public static class Asset {
            private String url;
        }
    }
}
