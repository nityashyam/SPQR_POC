package com.btireland.talos.mygroup.myproject.client.asset;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(name = "asset", url = "${application.example.asset.url}")
public interface ExampleAssetClient {

    @RequestMapping(method = POST, value = "/api/v1/asset", consumes = "application/json")
    ExampleAsset create(ExampleAssetCreateRequest request);

    @RequestMapping(method = GET, value = "/api/v1/asset/{assetId}", consumes = "application/json")
    ExampleAsset getByAssetId(@PathVariable("assetId") String assetId);
}
