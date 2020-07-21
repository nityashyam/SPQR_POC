package com.btireland.talos.mygroup.myproject.client.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleAsset {

    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime changedAt;
    private String name;
    private String type;
}
