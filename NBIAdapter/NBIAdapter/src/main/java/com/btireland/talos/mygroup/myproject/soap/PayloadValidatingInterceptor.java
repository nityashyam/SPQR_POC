package com.btireland.talos.mygroup.myproject.soap;

import com.btireland.talos.mygroup.myproject.soap.talosws.ErrorDetail;
import com.btireland.talos.mygroup.myproject.soap.talosws.ObjectFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.oxm.Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.server.endpoint.interceptor.AbstractFaultCreatingValidatingInterceptor;
import org.xml.sax.SAXParseException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.IOException;
import java.util.Locale;

/**
 * Spring provides a {@link org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor}.<br/>
 * It validates the client request and/or the server response.<br/>
 * Example of a validation error generated :
 * <pre>
 *  &lt;SOAP-ENV:Fault&gt;
 *     &lt;faultcode&gt;SOAP-ENV:Client&lt;/faultcode&gt;
 *     &lt;faultstring xml:lang="en"&gt;Validation error&lt;/faultstring&gt;
 *     &lt;detail&gt;
 *        &lt;spring-ws:ValidationError xmlns:spring-ws="http://springframework.org/spring-ws"&gt;cvc-complex-type.2.4.a: Invalid content was found starting with element '{"http://talos.btireland.com/examplews/schemas/v1":DATE_TIME_STAMP1}'. One of '{"http://talos.btireland.com/examplews/schemas/v1":DATE_TIME_STAMP}' is expected.&lt;/spring-ws:ValidationError&gt;
 *     &lt;/detail&gt;
 *  &lt;/SOAP-ENV:Fault&gt;
 * </pre>
 *
 * For security reasons, you don’t want to tell other application that you are using Spring-ws. <br/>
 * You can configure the {@link org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor}
 * to change the namespace and the name of the spring-ws:ValidationError element and the error message (faultstring element).<br/><br/>
 *
 * But here, with this validator, we add a CODE element in the detail section, to be consistent with the exception management in place, see {@link DetailSoapFaultAnnotationExceptionResolver}.<br/>
 * For this, we have to create a new validator. Java code has been mostly copied from the original {@link org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor}.<br/>
 * With this validator, the reponse generated is like
 * <pre>
 * &lt;SOAP-ENV:Fault&gt;
 *    &lt;faultcode&gt;SOAP-ENV:Client&lt;/faultcode&gt;
 *    &lt;faultstring xml:lang="en"&gt;Client request invalid&lt;/faultstring&gt;
 *    &lt;detail&gt;
 *       &lt;talos-ws:CODE xmlns:talos-ws="http://talos.btireland.com/ws/schemas/v1"&gt;500&lt;/talos-ws:CODE&gt;
 *       &lt;talos-ws:MESSAGE xmlns:talos-ws="http://talos.btireland.com/ws/schemas/v1"&gt;Error test&lt;/talos-ws:MESSAGE&gt;
 *       &lt;talos-ws:MESSAGE xmlns:talos-ws="http://talos.btireland.com/ws/schemas/v1"&gt;message 1&lt;/talos-ws:MESSAGE&gt;
 *       &lt;talos-ws:MESSAGE xmlns:talos-ws="http://talos.btireland.com/ws/schemas/v1"&gt;message 2&lt;/talos-ws:MESSAGE&gt;
 *    &lt;/detail&gt;
 * &lt;/SOAP-ENV:Fault&gt;
 * </pre>
 *
 */
@Slf4j
public class PayloadValidatingInterceptor extends AbstractFaultCreatingValidatingInterceptor {

    private Marshaller marshaller;

    public PayloadValidatingInterceptor(Marshaller marshaller){
        this.marshaller = marshaller;
        setFaultStringOrReason(SoapConstants.FAULT_VALIDATION_ERROR_MESSAGE);
    }

    /**
     * Returns the payload source of the given message.
     */
    @Override
    protected Source getValidationRequestSource(WebServiceMessage request) {
        return request.getPayloadSource();
    }

    /**
     * Returns the payload source of the given message.
     */
    @Override
    protected Source getValidationResponseSource(WebServiceMessage response) {
        return response.getPayloadSource();
    }

    /**
     * Template method that is called when the request message contains validation errors. This implementation logs all
     * errors, returns {@code false}, and creates a {@link SoapBody#addClientOrSenderFault(String, Locale) client or
     * sender} {@link SoapFault}, adding a {@link SoapFaultDetail} with all errors if the
     * {@code addValidationErrorDetail} property is {@code true}.
     * <p>
     * We override the one provided in {@link AbstractFaultCreatingValidatingInterceptor} to be able to return a fault message with
     * a detail containing code and message elements. By default, it will not contain the code element.
     * Code element is considered to be the detail block main element
     * Message elements are considered to be the detail block secondary elements
     *
     * @param messageContext the message context
     * @param errors         the validation errors
     * @return {@code true} to continue processing the request, {@code false} (the default) otherwise
     */
    @Override
    protected boolean handleRequestValidationErrors(MessageContext messageContext, SAXParseException[] errors) {
        for (SAXParseException error : errors) {
            logger.warn("XML validation error on request: " + error.getMessage());
        }
        if (messageContext.getResponse() instanceof SoapMessage) {
            SoapMessage response = (SoapMessage) messageContext.getResponse();
            SoapBody body = response.getSoapBody();
            SoapFault fault = body.addClientOrSenderFault(getFaultStringOrReason(), getFaultStringOrReasonLocale());
            if (getAddValidationErrorDetail()) {
                SoapFaultDetail detail = fault.addFaultDetail();

                ErrorDetail errorDetail = new ErrorDetail();
                errorDetail.setCODE(SoapConstants.FAULT_VALIDATION_ERROR_CODE);
                for (SAXParseException error : errors) {
                    errorDetail.getMESSAGE().add(error.getMessage());
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
        return false;
    }
}
