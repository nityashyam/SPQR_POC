package com.btireland.talos.mygroup.myproject.util;

import au.com.dius.pact.consumer.dsl.DslPart;
import com.btireland.talos.mygroup.myproject.client.asset.ExampleAsset;
import com.btireland.talos.mygroup.myproject.client.asset.ExampleAssetCreateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.pactfoundation.consumer.dsl.LambdaDsl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Build {@link ExampleAsset} objects for tests purpose
 */
public class ExampleAssetFactory {

    public static String asJson(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writeValueAsString(object);
    }

    /**
     * Asset sent to Asset Manager
     *
     * @return
     */
    public static ExampleAssetCreateRequest defaultAssetCreateRequest() throws NoSuchFieldException, IllegalAccessException {
        return ExampleAssetCreateRequest.builder()
                .name("LS")
                .type("WBMAX")
                .build();
    }

    public static String defaultAssetCreateRequestJsonFormat() throws Exception{
        return asJson(defaultAssetCreateRequest());
    }


    /**
     * Asset received from Asset Manager if we send the {@link ExampleAssetFactory#defaultAssetCreateRequest()}
     * request. Complete the request with the read-only fields : id, createAt, changedAt
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static ExampleAsset defaultAssetResponse() throws NoSuchFieldException, IllegalAccessException {
        ExampleAssetCreateRequest request = ExampleAssetFactory.defaultAssetCreateRequest();
        ExampleAsset exampleAsset = ExampleAsset.builder()
                .id("W00101234")
                .createdAt(LocalDateTime.parse("2020-04-20T12:02:28.803"))
                .changedAt(LocalDateTime.parse("2020-04-20T12:02:28.803"))
                .name(request.getName())
                .type(request.getType())
                .build();
        return exampleAsset;
    }

    public static String defaultAssetResponseJsonFormat() throws Exception{
        return defaultAssetResponsePactFormat().getBody().toString();
    }

    public static DslPart defaultAssetResponsePactFormat() throws Exception {
        ExampleAsset asset = ExampleAssetFactory.defaultAssetResponse();
        return LambdaDsl.newJsonBody((o) -> o
                .stringType("id", asset.getId())
                .datetime("createdAt", "yyyy-MM-dd'T'HH:mm:ss.SSS", asset.getCreatedAt().toInstant(getZoneOffset(asset.getCreatedAt())))
                .datetime("changedAt", "yyyy-MM-dd'T'HH:mm:ss.SSS", asset.getChangedAt().toInstant(getZoneOffset(asset.getChangedAt())))
                .stringType("name", asset.getName())
                .stringType("type", asset.getType())
        ).build();
    }

    private static ZoneOffset getZoneOffset(LocalDateTime date){
        return ZoneOffset.systemDefault().getRules().getOffset(date);
    }
}
