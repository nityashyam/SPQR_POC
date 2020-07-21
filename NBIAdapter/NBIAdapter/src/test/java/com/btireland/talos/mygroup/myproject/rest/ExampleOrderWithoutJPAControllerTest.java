package com.btireland.talos.mygroup.myproject.rest;

import com.btireland.talos.mygroup.myproject.dto.ExampleOrderDTO;
import com.btireland.talos.mygroup.myproject.exception.ResourceNotFoundException;
import com.btireland.talos.mygroup.myproject.service.ExampleOrderWithoutJPAService;
import com.btireland.talos.mygroup.myproject.tag.IntegrationTest;
import com.btireland.talos.mygroup.myproject.util.ExampleOrderDTOFactory;
import io.restassured.config.EncoderConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

/**
 * Test the controller layer only. @WebMvcTest will start a minimal spring context with Web related stuff. Other things like regular services or databases component
 * are not started. So, you need to mock your controller dependencies. That allows us to test this layer in isolation.
 * {@link com.btireland.talos.mygroup.myproject.service.ExampleOrderWithoutJPAService} is mocked and defined in TestRestConfiguration.class
 * <p>
 * We use rest-assured to make the test easy to write.
 */
@WebMvcTest
@ActiveProfiles("test")
@AutoConfigureJsonTesters
@Import(TestRestConfiguration.class)
@IntegrationTest
class ExampleOrderWithoutJPAControllerTest {

    private static final String BASE_URL = "/api/v1/exampleorderswithoutjpa";
    private static final String RESOURCE_URL = "/api/v1/exampleorderswithoutjpa/{id}";

    @Autowired
    private ExampleOrderWithoutJPAService exampleOrderService;

    @Autowired
    private JacksonTester<ExampleOrderDTO> json;

    @Autowired
    private BasicJsonTester basicJson;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void initialiseRestAssuredMockMvcWebApplicationContext() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssuredMockMvc.config = RestAssuredMockMvc.config().encoderConfig(new EncoderConfig(StandardCharsets.UTF_8.displayName(), StandardCharsets.UTF_8.displayName()));
    }
    @BeforeEach
    public void resetMock() {
        Mockito.reset(exampleOrderService);
    }

    @Nested
    @DisplayName("transfer order")
    class TransferOrder {
        @Test
        void transfer() {
            // configure the mock so it returns what we want
            ExampleOrderDTO expected = ExampleOrderDTOFactory.defaultExampleOrderResponse();
            ExampleOrderDTO request = ExampleOrderDTOFactory.defaultExampleOrderRequest();
            Mockito.when(exampleOrderService.transferOrder(request)).thenReturn(expected);

            // send the request, check header/status and extract the response to compare it with what we expect
            ExampleOrderDTO actual = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract().as(ExampleOrderDTO.class);

            Assertions.assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("400 return when ExampleOrder to be created is not valid")
        void transferShouldReturn400WhenObjectNotvalid() {
            ExampleOrderDTO request = ExampleOrderDTOFactory.defaultExampleOrderRequest();
            request.getItems().get(0).setQuantity(-1);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then().log().all()
                    .assertThat()
                    .statusCode(400);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("Get a 404 when the order is not found")
        void findByIdWhenNotFound() {

            Mockito.when(exampleOrderService.findById(1L)).thenThrow(ResourceNotFoundException.class);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(RESOURCE_URL, 1L)
                    .then()
                    .assertThat()
                    .statusCode(404);
        }

        @Test
        @DisplayName("Get an order when it is found")
        void findById() {

            // configure the mock so it returns what we want
            ExampleOrderDTO expected = ExampleOrderDTOFactory.defaultExampleOrderResponse();

            Mockito.when(exampleOrderService.findById(1L)).thenReturn(expected);

            String actualJson = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(RESOURCE_URL, 1L)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract().asString();

            Assertions.assertThat(basicJson.from(actualJson))
                    .isEqualToJson("ExampleOrderWithoutJPAControllerTest#findById.json", this.getClass(), JSONCompareMode.LENIENT)
                    .extractingJsonPathStringValue("$._links.self.href").matches(Pattern.compile("http://.*/api/v1/exampleorderswithoutjpa/1"));
        }
    }

    @Nested
    @DisplayName("notify change")
    class NotifyChange {
        @Test
        @DisplayName("400 returned when ExampleOrder to be changed is not valid")
        void notifyChangeShouldReturn400WhenObjectNotvalid() {
            ExampleOrderDTO request = ExampleOrderDTOFactory.defaultExampleOrderRequest();
            request.setExternalReference(null);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then().log().all()
                    .assertThat()
                    .statusCode(400);
        }
        @Test
        @DisplayName("Get a 404 when notifying a non existing order")
        void notifyChangeWhenNotFound() {
            ExampleOrderDTO request = ExampleOrderDTOFactory.defaultExampleOrderRequest();
            Mockito.when(exampleOrderService.notifyChange(1L, request)).thenThrow(ResourceNotFoundException.class);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .put(RESOURCE_URL, 1L)
                    .then()
                    .assertThat()
                    .statusCode(404);
        }

        @Test
        void notifyChange() {
            // configure the mock so it returns what we want
            ExampleOrderDTO expected = ExampleOrderDTOFactory.defaultExampleOrderResponse();
            ExampleOrderDTO request = ExampleOrderDTOFactory.defaultExampleOrderRequest();
            Mockito.when(exampleOrderService.notifyChange(1L, request)).thenReturn(expected);

            // send the request, check header/status and extract the response to compare it with what we expect
            ExampleOrderDTO actual = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .put(RESOURCE_URL, 1L)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract().as(ExampleOrderDTO.class);

            Assertions.assertThat(actual).isEqualTo(expected);
        }
    }

}