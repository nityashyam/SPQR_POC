package com.btireland.talos.mygroup.myproject.config;

import com.btireland.talos.mygroup.myproject.soap.DetailSoapFaultAnnotationExceptionResolver;
import com.btireland.talos.mygroup.myproject.soap.ExampleSoapEndpoint;
import com.btireland.talos.mygroup.myproject.soap.PayloadValidatingInterceptor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointExceptionResolver;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.SmartEndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadRootSmartSoapEndpointInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;

@EnableWs
@Configuration
public class WebServiceConfiguration extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);

        // we add the mapping /xsd/* so that the xsd schema bean defined in exampleSchema is available under the path /xsd/exampleWS-xsd-v1.xsd
        // by default, it will be available under /ws/exampleWS-xsd-v1.xsd. But inside the wsdl, the schema is imported under xsd/exampleWS-xsd-v1.xsd
        // so if we want that clients can download the wsdl from /ws/exampleWS-v1.wsdl and download the xsd from xsd/exampleWS-xsd-v1.xsd
        // we have to map 2 paths /xsd and /ws to this servlet
        return new ServletRegistrationBean<>(servlet, "/xsd/*", "/ws/*");
    }

    @Bean
    public EndpointExceptionResolver endpointExceptionResolver(Marshaller marshaller) {
        DetailSoapFaultAnnotationExceptionResolver exceptionResolver = new DetailSoapFaultAnnotationExceptionResolver(marshaller);
        exceptionResolver.setOrder(-1);
        return exceptionResolver;
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        // add here the global interceptors, meaning the one you want to run for every endpoints
    }

    /**
     * Definition of a validating interceptor to check client request against the XSD.
     * You can define another similar method if you have a second Web service with a different XSD.
     */
    @Bean()
    public SmartEndpointInterceptor exampleWsValidatingInterceptor(Marshaller marshaller) {

        PayloadValidatingInterceptor validator = new PayloadValidatingInterceptor(marshaller);
        validator.setValidateRequest(true);
        validator.setValidateResponse(true);
        validator.setXsdSchema(exampleWSSchemaV1());

        // we validate only the messages for exampleWS namespace
        return new PayloadRootSmartSoapEndpointInterceptor(validator, ExampleSoapEndpoint.NAMESPACE_PAYLOAD_URI, "");
    }

    @Bean(name = "exampleWS-v1")
    public SimpleWsdl11Definition examplewsV1() {
        return new SimpleWsdl11Definition(new ClassPathResource("exampleWS-v1.wsdl"));
    }

    @Bean(name = "exampleWS-xsd-v1")
    public XsdSchema exampleWSSchemaV1() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/exampleWS-xsd-v1.xsd"));
    }

    @Bean(name = "talosWS-xsd-v1")
    public XsdSchema talosSchemaV1() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/exampleWS-xsd-v1.xsd"));
    }

}
