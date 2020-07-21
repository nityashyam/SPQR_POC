package com.btireland.talos.mygroup.myproject.soap;

import com.btireland.talos.mygroup.myproject.soap.talosws.ErrorDetail;
import com.btireland.talos.mygroup.myproject.tag.UnitTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.xml.transform.TransformerFactoryUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class PayloadValidatingInterceptorTest {

    private static final String INVALID_MESSAGE = "PayloadValidatingInterceptorTest-invalidMessage.xml";
    private static final String VALID_MESSAGE = "PayloadValidatingInterceptorTest-validMessage.xml";
    private static final String SCHEMA = "PayloadValidatingInterceptorTest.xsd";

    private SaajSoapMessageFactory soap11Factory;
    private Transformer transformer;
    private PayloadValidatingInterceptor validatingInterceptor;

    @BeforeEach
    public void setUp() throws Exception {
        soap11Factory = new SaajSoapMessageFactory(MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL));
        transformer = TransformerFactoryUtils.newInstance().newTransformer();

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(ErrorDetail.class);
        validatingInterceptor = new PayloadValidatingInterceptor(marshaller);
        validatingInterceptor.setSchema(new ClassPathResource(SCHEMA, getClass()));
        validatingInterceptor.setValidateRequest(true);
        validatingInterceptor.setValidateResponse(true);
        validatingInterceptor.afterPropertiesSet();
    }

    @Test
    @DisplayName("ERROR_DETAIL must contain the error code 400 and all the error validation messages")
    public void testHandleInvalidRequestSoap11() throws TransformerException, IOException, SAXException, SOAPException {
        SoapMessage invalidSoapMessage = soap11Factory.createWebServiceMessage();
        InputStream inputStream = getClass().getResourceAsStream(INVALID_MESSAGE);
        transformer.transform(new StreamSource(inputStream), invalidSoapMessage.getPayloadResult());
        MessageContext context = new DefaultMessageContext(invalidSoapMessage, soap11Factory);

        boolean result = validatingInterceptor.handleRequest(context, null);

        assertThat(result).as("Validator considers this invalid request as valid").isFalse();
        assertThat(context.hasResponse()).as("Context has no response").isTrue();

        // we check the soap body, it must contain a fault like this
        // <SOAP-ENV:Fault>
        //    <faultcode>SOAP-ENV:Client</faultcode>
        //    <faultstring xml:lang="en">Client request invalid</faultstring>
        //    <detail>
        //       <ns4:ERROR_DETAIL xmlns:ns2="http://talos.btireland.com/ws/test" xmlns:ns3="http://retail.btireland.com/siebel/schemas/v1" xmlns:ns4="http://talos.btireland.com/ws/schemas/v1">
        //          <ns4:CODE>400</ns4:CODE>
        //          <ns4:MESSAGE>cvc-maxLength-valid: Value 'bbb' with length = '3' is not facet-valid with respect to maxLength '2' for type...</ns4:MESSAGE>
        //          <ns4:MESSAGE>cvc-type.3.1.3: The value 'bbb' of element 'v1:ID' is not valid.</ns4:MESSAGE>
        //       </ns4:ERROR_DETAIL>
        //    </detail>
        // </SOAP-ENV:Fault>

        SOAPMessage response = ((SaajSoapMessage) context.getResponse()).getSaajMessage();
        assertThat(response.getSOAPBody().hasFault()).as("Response has no fault").isTrue();
        SOAPFault fault = response.getSOAPBody().getFault();
        assertThat(fault.getFaultCode()).as("Invalid fault code on fault").isEqualTo("SOAP-ENV:Client");
        assertThat(fault.getFaultString()).as("Invalid fault string on fault").isEqualTo("Client request invalid");

        Detail  faultDetail = fault.getDetail();
        Iterator<DetailEntry> detailEntries = faultDetail.getDetailEntries();
        DetailEntry detailEntry = detailEntries.next();
        Assertions.assertThat(detailEntry.getElementQName()).isEqualTo(new QName("http://talos.btireland.com/ws/schemas/v1", "ERROR_DETAIL"));

        NodeList nodeList = detailEntry.getChildNodes();
        Assertions.assertThat(nodeList.getLength()).as("ERROR_DETAIL must have a CODE element and 2 MESSAGE elements").isEqualTo(3);

        org.w3c.dom.Node code = nodeList.item(0);
        Assertions.assertThat(code.getNamespaceURI()).isEqualTo("http://talos.btireland.com/ws/schemas/v1");
        Assertions.assertThat(code.getLocalName()).isEqualTo("CODE");
        Assertions.assertThat(code.getTextContent()).as("Invalid detail code").isEqualTo("400");

        Node message = nodeList.item(1);
        Assertions.assertThat(message.getNamespaceURI()).isEqualTo("http://talos.btireland.com/ws/schemas/v1");
        Assertions.assertThat(message.getLocalName()).isEqualTo("MESSAGE");
        Assertions.assertThat(message.getTextContent()).as("Invalid detail message").matches(".*Value 'bbb' with length = '3' is not facet-valid with respect to maxLength '2'.*");

        Node message2 = nodeList.item(2);
        Assertions.assertThat(message2.getNamespaceURI()).isEqualTo("http://talos.btireland.com/ws/schemas/v1");
        Assertions.assertThat(message2.getLocalName()).isEqualTo("MESSAGE");
        Assertions.assertThat(message2.getTextContent()).as("Invalid detail message").matches(".*value 'bbb' of element '.*ID' is not valid.*");

    }

    @Test
    @DisplayName("valid request must be accepted")
    public void testHandleValidRequest() throws Exception {

        SoapMessage validSoapMessage = soap11Factory.createWebServiceMessage();
        InputStream inputStream = getClass().getResourceAsStream(VALID_MESSAGE);
        transformer.transform(new StreamSource(inputStream), validSoapMessage.getPayloadResult());
        MessageContext context = new DefaultMessageContext(validSoapMessage, soap11Factory);

        boolean result = validatingInterceptor.handleRequest(context, null);

        assertThat(result).as("Validator considers this valid request as invalid").isTrue();
        assertThat(context.hasResponse()).as("Context has a response").isFalse();
    }

    @Test
    @DisplayName("Invalid response must be rejected")
    public void testHandleInvalidResponse() throws Exception {

        SoapMessage invalidSoapMessage = soap11Factory.createWebServiceMessage();
        InputStream inputStream = getClass().getResourceAsStream(INVALID_MESSAGE);
        transformer.transform(new StreamSource(inputStream), invalidSoapMessage.getPayloadResult());
        MessageContext context = new DefaultMessageContext(invalidSoapMessage, soap11Factory);
        context.setResponse(invalidSoapMessage);

        boolean result = validatingInterceptor.handleResponse(context, null);

        assertThat(result).as("Validator considers this valid response as invalid").isFalse();
    }

    @Test
    @DisplayName("valid response must be accepted")
    public void testHandleValidResponse() throws Exception {

        SoapMessage validSoapMessage = soap11Factory.createWebServiceMessage();
        InputStream inputStream = getClass().getResourceAsStream(VALID_MESSAGE);
        transformer.transform(new StreamSource(inputStream), validSoapMessage.getPayloadResult());
        MessageContext context = new DefaultMessageContext(validSoapMessage, soap11Factory);
        context.setResponse(validSoapMessage);

        boolean result = validatingInterceptor.handleResponse(context, null);

        assertThat(result).as("Validator considers this valid response as invalid").isTrue();
    }
}