package com.btireland.talos.mygroup.myproject.service;

import com.btireland.talos.mygroup.myproject.client.asset.ExampleAsset;
import com.btireland.talos.mygroup.myproject.client.asset.ExampleAssetClient;
import com.btireland.talos.mygroup.myproject.exception.ResourceNotFoundException;
import feign.FeignException;
import org.springframework.stereotype.Service;

@Service
public class ExampleAssetService {

    private ExampleAssetClient assetClient;

    public ExampleAssetService(ExampleAssetClient assetClient) {
        this.assetClient = assetClient;
    }

    /**
     *
     * @param assetId
     * @return
     * @throws com.btireland.talos.mygroup.myproject.exception.ResourceNotFoundException if the asset does not exist
     */
    public ExampleAsset getByAssetId(String assetId) {
        try {
            return assetClient.getByAssetId(assetId);
        } catch (FeignException.NotFound nfe){
            throw new ResourceNotFoundException("Asset " + assetId + " does not exist", nfe);
        }
    }

}
