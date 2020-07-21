package com.btireland.talos.mygroup.myproject.service;

import com.btireland.talos.mygroup.myproject.client.asset.ExampleAsset;
import com.btireland.talos.mygroup.myproject.exception.ResourceNotFoundException;
import com.btireland.talos.mygroup.myproject.tag.IntegrationTest;
import com.btireland.talos.mygroup.myproject.util.ExampleAssetFactory;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Class that shows how to do an integration when the code is calling an external REST service.
 * Here, the service depends on a REST client.
 *
 * We will use Wiremock to stub response.
 */
@IntegrationTest
@SpringBootTest
@ActiveProfiles("test")
class ExampleAssetServiceTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private ExampleAssetService service;

    @BeforeAll
    public static void startStubServer(){
        wireMockServer = new WireMockServer(8089); //No-args constructor will start on port 8080, no HTTPS
        wireMockServer.start();
        configureFor("localhost", 8089);
    }

    @AfterAll
    public static void stopStubServer(){
        wireMockServer.stop();
    }

    @AfterEach
    public void resetStubMappings(){
        wireMockServer.resetAll();
    }

    @DisplayName("get an asset by id")
    @Test
    public void testGet() throws Exception {
        // Configure Wiremock to send an asset when receiving this request
        stubFor(get(urlEqualTo("/api/v1/asset/W00101234"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(ExampleAssetFactory.defaultAssetResponseJsonFormat())));

        ExampleAsset expected = ExampleAssetFactory.defaultAssetResponse();

        // The service should call the mock server
        ExampleAsset actual = service.getByAssetId("W00101234");

        Assertions.assertThat(actual).isEqualTo(expected);
        // we check that the code sent the expected GET request
        WireMock.verify(getRequestedFor(urlEqualTo("/api/v1/asset/W00101234"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(WireMock.absent())
        );
    }

    @DisplayName("get an asset that does not exist throws a ResourceNotFoundException")
    @Test
    public void testGetWhenAssetNotFound() throws Exception {
        stubFor(get(urlEqualTo("/api/v1/asset/W00101234"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(404)));

        // The service must throw a ResourceNotFoundException
        Throwable thrown = Assertions.catchThrowable(() -> { service.getByAssetId("W00101234"); });

        Assertions.assertThat(thrown).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("does not exist");

        // check that we sent the correct json request to notcom
        WireMock.verify(getRequestedFor(urlEqualTo("/api/v1/asset/W00101234"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(WireMock.absent())
        );
    }
}