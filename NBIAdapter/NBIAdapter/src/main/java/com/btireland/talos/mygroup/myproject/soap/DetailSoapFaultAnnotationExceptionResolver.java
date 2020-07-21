package com.btireland.talos.mygroup.myproject.soap;

import com.btireland.talos.mygroup.myproject.exception.SoapFaultException;
import com.btireland.talos.mygroup.myproject.soap.talosws.ErrorDetail;
import com.btireland.talos.mygroup.myproject.soap.talosws.ObjectFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.oxm.Marshaller;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultAnnotationExceptionResolver;

import javax.xml.transform.Result;
import java.io.IOException;

/**
 * By default, if an exception is thrown and comes in the SOAP layer, it will produce a fault like this
 * <pre>
 * &lt;SOAP-ENV:Fault&gt;
 *    &lt;faultcode&gt;SOAP-ENV:Client&lt;/faultcode&gt;
 *    &lt;faultstring xml:lang="en"&gt;Validation error&lt;/faultstring&gt;
 *    &lt;detail&gt;
 *       &lt;spring-ws:ValidationError xmlns:spring-ws="http://springframework.org/spring-ws"&gt;cvc-complex-type.2.4.a: Invalid content was found starting with element '{"http://talos.btireland.com/examplews/schemas/v1":DATE_TIME_STAMP1}'. One of '{"http://talos.btireland.com/examplews/schemas/v1":DATE_TIME_STAMP}' is expected.&lt;/spring-ws:ValidationError&gt;
 *    &lt;/detail&gt;
 * &lt;/SOAP-ENV:Fault&gt;
 * </pre>
 *
 * If you want to add a detail element with a custom code and message, you need to implement an {@link org.springframework.ws.server.EndpointExceptionResolver}
 * This class add such a detail element and produces a fault like
 *
 * <pre>
 * &lt;SOAP-ENV:Fault&gt;
 *    &lt;faultcode&gt;SOAP-ENV:Client&lt;/faultcode&gt;
 *    &lt;faultstring xml:lang="en"&gt;Client request invalid&lt;/faultstring&gt;
 *    &lt;detail&gt;
 *       &lt;talos-ws:CODE xmlns:talos-ws="http://talos.btireland.com/ws/schemas/v1"&gt;500&lt;/talos-ws:CODE&gt;
 *       &lt;talos-ws:MESSAGE xmlns:talos-ws="http://talos.btireland.com/ws/schemas/v1"&gt;Error test&lt;/talos-ws:MESSAGE&gt;
 *       &lt;talos-ws:MESSAGE xmlns:talos-ws="http://talos.btireland.com/ws/schemas/v1"&gt;message 1&lt;/talos-ws:MESSAGE&gt;
 *    &lt;/detail&gt;
 * &lt;/SOAP-ENV:Fault&gt;
 * </pre>
 *
 * It only takes into account the exception of type {@link SoapFaultException}
 */
@Slf4j
public class DetailSoapFaultAnnotationExceptionResolver extends SoapFaultAnnotationExceptionResolver {

    private Marshaller marshaller;

    public DetailSoapFaultAnnotationExceptionResolver(Marshaller marshaller){
        this.marshaller = marshaller;
    }

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        if (ex instanceof SoapFaultException) {
            SoapFaultException sfe = (SoapFaultException) ex;
            SoapFaultDetail detail = fault.addFaultDetail();

            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setCODE(sfe.getCode());
            errorDetail.getMESSAGE().add(sfe.getMessage());
            if (sfe.getDetailedMessages() != null && !sfe.getDetailedMessages().isEmpty()){
                for(String msg : sfe.getDetailedMessages()){
                    errorDetail.getMESSAGE().add(msg);
                }
            }
            Result result = detail.getResult();
            ObjectFactory objectFactory = new ObjectFactory();
            try {
                marshaller.marshal(objectFactory.createERRORDETAIL(errorDetail), result);
            } catch (IOException e) {
                log.error("failed to marshal ErrorDetail during Soap fault detail generation", e);
            }
        }
    }
}
