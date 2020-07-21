package com.btireland.talos.mygroup.myproject.soap;

import com.btireland.talos.mygroup.myproject.exception.ClientSoapFaultException;
import com.btireland.talos.mygroup.myproject.soap.examplews.Notification;
import com.btireland.talos.mygroup.myproject.soap.examplews.ObjectFactory;
import com.btireland.talos.mygroup.myproject.soap.examplews.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.*;
import org.springframework.ws.soap.server.endpoint.annotation.SoapAction;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Slf4j
@Endpoint
@Namespace(prefix = "v1", uri = "http://talos.btireland.com/examplews/schemas/v1")
public class ExampleSoapEndpoint {

    public static final String NAMESPACE_URI = "http://talos.btireland.com/examplews/v1";
    public static final String NAMESPACE_PAYLOAD_URI = "http://talos.btireland.com/examplews/schemas/v1";

    @SoapAction("http://examplews.talos.btireland.com/accept/v1")
    public void receiveAcceptNotification(@RequestPayload Notification request) throws Exception {
        log.info("do something with my request");
        log.info("in this example, we don’t provide a response");
        if ("Exception".equals(request.getHEADER().getDATACONTRACT())) {
            throw new Exception("Exception case");
        }
    }

    /**
     * We want to get the order with the orderId as parameter. Makes sense.
     * But, to keep the WSDL simple, the message sent is
     * <pre>
     * &lt;soapenv:Body&gt;
     *    &lt;v1:ORDER_ID&gt;200&lt;/v1:ORDER_ID&gt;
     * &lt;/soapenv:Body&gt;
     * </pre>
     *
     * &lt;v1:ORDER_ID&gt; is not a complex type but a simple one (in the xsd). So, the maven plugin won’t generate any special class for this.
     * So we can’t map it like in the method {@link ExampleSoapEndpoint#receiveAcceptNotification(Notification)} in a String object.
     * We then have to use XPATH to get the order_id value from the message.
     *
     * If you want to use an object as before, you can. But you need to define a wrapper object around ORDER_ID element so that it becomes a complex type
     * and JAXB generates a corresponding class.
     * You could for example send a message like this
     * <pre>
     * &lt;soapenv:Body&gt;
     *    &lt;v1:ORDER_REQUEST&gt;
     *       &lt;v1:ORDER_ID&gt;200&lt;/v1:ORDER_ID&gt;
     *    &lt;/v1:ORDER_ID&gt;
     * &lt;/soapenv:Body&gt;
     * </pre>
     * Your choice !
     * @param orderId order id used to retrieve the order
     * @return an {@link javax.xml.bind.JAXBElement}. Object generating by the JAXB maven plugin. We have to return a JAXBElement so that JAXB knows how to generate XML from it.
     * JAXBElement is just a wrapper around another object adding XML relating things like the namespace or the element name.
     */
    @SoapAction("http://examplews.talos.btireland.com/getorder/v1")
    @ResponsePayload
    public JAXBElement<Order> getOrder(@XPathParam("/v1:ORDER_ID/text()") String orderId) throws ClientSoapFaultException {

        // to test the exception use case, we throw an exception for this particular orderId
        if ("500".equals(orderId)){
            ClientSoapFaultException ex = new ClientSoapFaultException("500", "Error test");
            ex.addDetailMessage(List.of("message 1", "message 2"));
            throw ex;
        }
        Order order = new Order();
        order.setORDERNUMBER(orderId);
        order.setNAME("name");
        ObjectFactory objectFactory = new ObjectFactory();
        return objectFactory.createORDER(order);
    }
}
