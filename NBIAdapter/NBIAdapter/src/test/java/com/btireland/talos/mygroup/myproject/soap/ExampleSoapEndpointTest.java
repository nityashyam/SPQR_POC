package com.btireland.talos.mygroup.myproject.soap;

import com.btireland.talos.mygroup.myproject.tag.IntegrationTest;
import io.restassured.RestAssured;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * Test the controller layer only. @WebMvcTest will start a minimal spring context with Web related stuff. Other things like regular services or databases component
 * are not started. So, you need to mock your controller dependencies. That allows us to test this layer in isolation.
 * ExampleOrderFacade is mocked and defined in TestRestConfiguration.class
 *
 * We use rest-assured to make the test easy to write.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@IntegrationTest
class ExampleSoapEndpointTest {

    private static final String BASE_URL = "/ws";
    private static final String SOAP_ACTION_HEADER = "SOAPAction";
    private static final String SOAP_ACTION_ACCEPT = "http://examplews.talos.btireland.com/accept/v1";
    private static final String SOAP_ACTION_GETORDER = "http://examplews.talos.btireland.com/getorder/v1";
    private static final String XML_CONTENT_TYPE = "text/xml";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void initialiseRestAssuredMockMvcWebApplicationContext(@Autowired WebApplicationContext webApplicationContext) {
        //RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        RestAssured.port = this.port;
    }

    @Test
    @DisplayName("Send a accept notification to the SOAP web service")
    void receiveAcceptNotification() throws IOException {

        RestAssured.given().log().all()
                .contentType(XML_CONTENT_TYPE)
                .header(SOAP_ACTION_HEADER, SOAP_ACTION_ACCEPT)
                .body(IOUtils.toString(new ClassPathResource("ExampleSoapEndpointTest#receiveAcceptNotification.xml", this.getClass()).getInputStream(), StandardCharsets.UTF_8))
        .when()
                .post(BASE_URL)
        .then()
                .assertThat()
                .statusCode(202);
    }

    @Test
    @DisplayName("Send a accept notification to the SOAP web service. This notification does not respect the xsd")
    void receiveAcceptNotificationWithValidationError() throws IOException {

        RestAssured.given().log().all()
                .contentType(XML_CONTENT_TYPE)
                .header(SOAP_ACTION_HEADER, SOAP_ACTION_ACCEPT)
                .body(IOUtils.toString(new ClassPathResource("ExampleSoapEndpointTest#receiveAcceptNotificationWithValidationError.xml", this.getClass()).getInputStream(), StandardCharsets.UTF_8))
        .when()
                .post(BASE_URL)
        .then()
                .assertThat()
                .statusCode(500)
                .body("Envelope.Body.Fault.faultcode", Matchers.equalTo("SOAP-ENV:Client"))
                .body("Envelope.Body.Fault.faultstring", Matchers.equalTo("Client request invalid"))
                .body("Envelope.Body.Fault.detail.ERROR_DETAIL.CODE", Matchers.equalTo("400"))
                .body("Envelope.Body.Fault.detail.ERROR_DETAIL.MESSAGE[0]", Matchers.containsString("but the number of total digits has been limited to 3"));
    }

    @Test
    @DisplayName("Get an order from an orderId")
    void getOrder() throws IOException {

        RestAssured.given().log().all()
                .contentType(XML_CONTENT_TYPE)
                .header(SOAP_ACTION_HEADER, SOAP_ACTION_GETORDER)
                .body(IOUtils.toString(new ClassPathResource("ExampleSoapEndpointTest#getOrder.xml", this.getClass()).getInputStream(), StandardCharsets.UTF_8))
        .when()
                .post(BASE_URL)
        .then()
                .assertThat()
                .statusCode(200)
                .body("Envelope.Body.ORDER.ORDER_NUMBER", Matchers.equalTo("200"))
                .body("Envelope.Body.ORDER.NAME", Matchers.equalTo("name"));
    }

    @Test
    @DisplayName("Get an order from an orderId, it does not exist so we throw an exception")
    void getOrderThrowsException() throws IOException {

        RestAssured.given().log().all()
                .contentType(XML_CONTENT_TYPE)
                .header(SOAP_ACTION_HEADER, SOAP_ACTION_GETORDER)
                .body(IOUtils.toString(new ClassPathResource("ExampleSoapEndpointTest#getOrderThrowsException.xml", this.getClass()).getInputStream(), StandardCharsets.UTF_8))
        .when()
                .post(BASE_URL)
        .then()
                .assertThat()
                .statusCode(500)
                .body("Envelope.Body.Fault.faultcode", Matchers.equalTo("SOAP-ENV:Client"))
                .body("Envelope.Body.Fault.faultstring", Matchers.equalTo("Client request invalid"))
                .body("Envelope.Body.Fault.detail.ERROR_DETAIL.CODE", Matchers.equalTo("500"))
                .body("Envelope.Body.Fault.detail.ERROR_DETAIL.MESSAGE[0]", Matchers.equalTo("Error test"))
                .body("Envelope.Body.Fault.detail.ERROR_DETAIL.MESSAGE[1]", Matchers.equalTo("message 1"))
                .body("Envelope.Body.Fault.detail.ERROR_DETAIL.MESSAGE[2]", Matchers.equalTo("message 2"));
    }
}