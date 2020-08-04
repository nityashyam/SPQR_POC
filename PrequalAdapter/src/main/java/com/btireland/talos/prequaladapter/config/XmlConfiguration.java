package com.btireland.talos.prequaladapter.config;

import com.btireland.talos.prequaladapter.soap.SOAPConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Configuration
public class XmlConfiguration {

    @Bean
    public Jaxb2Marshaller jaxb2marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
       // marshaller.setPackagesToScan();
        marshaller.setContextPath("com.btireland.talos.prequaladapter.soap");
        return marshaller;
    }

    @Bean
    public SOAPConnector soapConnector(Jaxb2Marshaller marshaller) {
        SOAPConnector client = new SOAPConnector();
       // client.setDefaultUri("http://localhost:8080/service/student-details");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

}
