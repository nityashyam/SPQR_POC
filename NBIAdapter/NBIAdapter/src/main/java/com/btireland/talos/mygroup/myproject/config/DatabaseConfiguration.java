package com.btireland.talos.mygroup.myproject.config;

import com.btireland.talos.mygroup.myproject.repository.JpaRsqlRepositoryImpl;
import io.github.perplexhub.rsql.RSQLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableJpaRepositories(basePackages = "com.btireland.talos.mygroup.myproject.repository", repositoryBaseClass = JpaRsqlRepositoryImpl.class)
@EnableTransactionManagement
@Import(RSQLConfig.class)
public class DatabaseConfiguration {

    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);
}
