package com.btireland.talos.mygroup.myproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class XmlConfiguration {

    @Bean
    public Marshaller jaxb2marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.btireland.talos.mygroup.myproject.soap");
        return marshaller;
    }
}
