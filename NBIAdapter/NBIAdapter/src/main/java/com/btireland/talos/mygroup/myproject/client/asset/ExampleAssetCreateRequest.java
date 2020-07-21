package com.btireland.talos.mygroup.myproject.client.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleAssetCreateRequest {

    private String name;
    private String type;
}
