package com.btireland.talos.mygroup.myproject.client.asset;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.btireland.talos.mygroup.myproject.tag.IntegrationTest;
import com.btireland.talos.mygroup.myproject.util.ExampleAssetFactory;
import feign.FeignException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Purpose of these tests is to make sure the feign client is created properly by Spring and that the json mapping is working
 */
@IntegrationTest
@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("AssetClientTest")
class ExampleAssetClientTest {

    @Autowired
    private ExampleAssetClient client;

    @Pact(provider = "NBIAdapter-provider", consumer = "NBIAdapter-consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) throws Exception {
        return builder
                .uponReceiving("create an asset request")
                .path("/api/v1/asset")
                .method("POST")
                .body(ExampleAssetFactory.defaultAssetCreateRequestJsonFormat())
                .willRespondWith()
                .status(201)
                .body(ExampleAssetFactory.defaultAssetResponsePactFormat())
                .toPact();
    }

    @PactTestFor(pactMethod = "createPact", port = "8089")
    @Test
    @DisplayName("send a new asset to Asset manager, check that the json is sent and parsed correctly")
    public void testCreate() throws Exception {

        ExampleAssetCreateRequest request = ExampleAssetFactory.defaultAssetCreateRequest();
        ExampleAsset expected = ExampleAssetFactory.defaultAssetResponse();

        // Make the REST call
        ExampleAsset actual = client.create(request);

        // Check that the object is what we expect, so json is read correctly
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Pact(provider = "NBIAdapter-provider", consumer = "NBIAdapter-consumer")
    public RequestResponsePact getPact(PactDslWithProvider builder) throws Exception {
        return builder
                .uponReceiving("create an asset request")
                .path("/api/v1/asset/W00101234")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(ExampleAssetFactory.defaultAssetResponsePactFormat())
                .toPact();
    }
    @PactTestFor(pactMethod = "getPact", port = "8089")
    @Test
    @DisplayName("get an asset from the Asset manager by id")
    public void testGet() throws Exception {

        ExampleAsset expected = ExampleAssetFactory.defaultAssetResponse();

        ExampleAsset actual = client.getByAssetId("W00101234");

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Pact(provider = "NBIAdapter-provider", consumer = "NBIAdapter-consumer")
    public RequestResponsePact get404Pact(PactDslWithProvider builder) throws Exception {
        return builder
                .uponReceiving("create an asset request")
                .path("/api/v1/asset/W00101666")
                .method("GET")
                .willRespondWith()
                .status(404)
                .toPact();
    }
    @PactTestFor(pactMethod = "get404Pact", port = "8089")
    @Test
    @DisplayName("asset is not found")
    public void testGet404() throws Exception {

        Throwable thrown = Assertions.catchThrowable(() -> { client.getByAssetId("W00101666");});

        Assertions.assertThat(thrown).isInstanceOf(FeignException.NotFound.class);
    }
}