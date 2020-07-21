package com.btireland.talos.mygroup.myproject.soap;

import com.btireland.talos.mygroup.myproject.exception.SoapFaultException;
import com.btireland.talos.mygroup.myproject.soap.talosws.ErrorDetail;
import com.btireland.talos.mygroup.myproject.tag.UnitTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.util.Iterator;

@UnitTest
class DetailSoapFaultAnnotationExceptionResolverTest {

    private DetailSoapFaultAnnotationExceptionResolver resolver;

    @BeforeEach
    public void setUp() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(ErrorDetail.class);
        resolver = new DetailSoapFaultAnnotationExceptionResolver(marshaller);
    }

    @Test
    void testResolve() throws SOAPException {
        MessageFactory saajFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SoapMessageFactory factory = new SaajSoapMessageFactory(saajFactory);
        MessageContext context = new DefaultMessageContext(factory);

        boolean result = resolver.resolveException(context, null, new MyClientException("code 1", "Error test"));
        Assertions.assertThat(result).isTrue();
        Assertions.assertThat(context.hasResponse()).isTrue();

        SOAPMessage response = ((SaajSoapMessage) context.getResponse()).getSaajMessage();
        Assertions.assertThat(response.getSOAPBody().hasFault()).as("Resonse has no fault").isTrue();

        SOAPFault fault = response.getSOAPBody().getFault();
        Assertions.assertThat(fault.getFaultCode()).as("Invalid fault code on fault").isEqualTo("SOAP-ENV:Client");
        Assertions.assertThat(fault.getFaultString()).as("Invalid fault string on fault").isEqualTo("Client error");

        Detail  faultDetail = fault.getDetail();
        Iterator<DetailEntry> detailEntries = faultDetail.getDetailEntries();
        DetailEntry detailEntry = detailEntries.next();
        Assertions.assertThat(detailEntry.getElementQName()).isEqualTo(new QName("http://talos.btireland.com/ws/schemas/v1", "ERROR_DETAIL"));

        Node code = detailEntry.getFirstChild();
        Assertions.assertThat(code.getNamespaceURI()).isEqualTo("http://talos.btireland.com/ws/schemas/v1");
        Assertions.assertThat(code.getLocalName()).isEqualTo("CODE");
        Assertions.assertThat(code.getTextContent()).as("Invalid detail code").isEqualTo("code 1");

        Node message = detailEntry.getLastChild();
        Assertions.assertThat(message.getNamespaceURI()).isEqualTo("http://talos.btireland.com/ws/schemas/v1");
        Assertions.assertThat(message.getLocalName()).isEqualTo("MESSAGE");
        Assertions.assertThat(message.getTextContent()).as("Invalid detail message").isEqualTo("Error test");

    }

    @SoapFault(faultCode = FaultCode.CLIENT, faultStringOrReason = "Client error")
    @SuppressWarnings("serial")
    public class MyClientException extends SoapFaultException {
        public MyClientException(String code, String message) {
            super(code, message);
        }
    }
}