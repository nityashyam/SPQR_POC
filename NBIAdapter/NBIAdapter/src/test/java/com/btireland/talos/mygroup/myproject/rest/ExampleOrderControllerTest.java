package com.btireland.talos.mygroup.myproject.rest;

import com.btireland.talos.mygroup.myproject.dto.ExampleOrderDTO;
import com.btireland.talos.mygroup.myproject.exception.ResourceNotFoundException;
import com.btireland.talos.mygroup.myproject.facade.ExampleOrderFacade;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

/**
 * Test the controller layer only. @WebMvcTest will start a minimal spring context with Web related stuff. Other things like regular services or databases component
 * are not started. So, you need to mock your controller dependencies. That allows us to test this layer in isolation.
 * ExampleOrderFacade is mocked and defined in TestRestConfiguration.class
 * <p>
 * We use rest-assured to make the test easy to write.
 */
@WebMvcTest
@ActiveProfiles("test")
@AutoConfigureJsonTesters
@Import(TestRestConfiguration.class)
@IntegrationTest
class ExampleOrderControllerTest {

    private static final String BASE_URL = "/api/v1/exampleorders";
    private static final String RESOURCE_URL = "/api/v1/exampleorders/{id}";

    @Autowired
    private ExampleOrderFacade exampleOrderFacade;

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
        Mockito.reset(exampleOrderFacade);
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        void create() {
            // configure the mock facade so it returns what we want
            ExampleOrderDTO expected = ExampleOrderDTOFactory.defaultExampleOrderResponse();
            ExampleOrderDTO request = ExampleOrderDTOFactory.defaultExampleOrderRequest();
            Mockito.when(exampleOrderFacade.create(request)).thenReturn(expected);

            // send the request, check header/status and extract the response to compare it with what we expect
            ExampleOrderDTO actual = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .assertThat()
                    .statusCode(201)
                    .extract().as(ExampleOrderDTO.class);

            Assertions.assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("400 return when ExampleOrder to be created is not valid")
        void createShouldReturn400WhenObjectNotvalid() {
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

            Mockito.when(exampleOrderFacade.findById(1L)).thenThrow(ResourceNotFoundException.class);

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

            // configure the mock facade so it returns what we want
            ExampleOrderDTO expected = ExampleOrderDTOFactory.defaultExampleOrderResponse();

            Mockito.when(exampleOrderFacade.findById(1L)).thenReturn(expected);

            String actualJson = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(RESOURCE_URL, 1L)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract().asString();

            Assertions.assertThat(basicJson.from(actualJson))
                    .isEqualToJson("ExampleOrderControllerTest#findById.json", this.getClass(), JSONCompareMode.LENIENT)
                    .extractingJsonPathStringValue("$._links.self.href").matches(Pattern.compile("http://.*/api/v1/exampleorders/1"));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("400 return when ExampleOrder to be updated is not valid")
        void updateShouldReturn400WhenObjectNotvalid() {
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
        @DisplayName("Get a 404 when updating a non existing order")
        void updateWhenNotFound() {
            ExampleOrderDTO request = ExampleOrderDTOFactory.defaultExampleOrderRequest();
            Mockito.when(exampleOrderFacade.update(1L, request)).thenThrow(ResourceNotFoundException.class);

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
        void update() {
            // configure the mock facade so it returns what we want
            ExampleOrderDTO expected = ExampleOrderDTOFactory.defaultExampleOrderResponse();
            ExampleOrderDTO request = ExampleOrderDTOFactory.defaultExampleOrderRequest();
            Mockito.when(exampleOrderFacade.update(1L, request)).thenReturn(expected);

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

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        @DisplayName("Get a 204 when deleting a non existing order")
        void deleteWhenNotFound() {
            Mockito.doThrow(ResourceNotFoundException.class).when(exampleOrderFacade).delete(1L);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete(RESOURCE_URL, 1L)
                    .then()
                    .assertThat()
                    .statusCode(204);
        }

        @Test
        @DisplayName("Get a 204 when deleting an existing order")
        void delete() {

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete(RESOURCE_URL, 1L)
                    .then()
                    .assertThat()
                    .statusCode(204);
        }

    }

    @Nested
    @DisplayName("findAll")
    class FindAll {
        @Test
        void findAll() {
            // the mock will return the second page of size 1
            PageRequest pageRequest = PageRequest.of(1, 1);
            ExampleOrderDTO orderDTO = ExampleOrderDTOFactory.defaultExampleOrderResponse();
            Page<ExampleOrderDTO> orderDTOs = new PageImpl<>(Arrays.asList(orderDTO), pageRequest, 3);

            Mockito.when(exampleOrderFacade.findAll("", pageRequest)).thenReturn(orderDTOs);

            String actualJson = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(BASE_URL + "?filters=&page=1&size=1")
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract().asString();

            // We can compare the JSON from the response with a JSON file.
            Assertions.assertThat(basicJson.from(actualJson))
                    .isEqualToJson("ExampleOrderControllerTest#findAll.json", this.getClass(), JSONCompareMode.LENIENT);
        }
    }
}