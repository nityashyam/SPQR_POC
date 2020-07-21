package com.btireland.talos.mygroup.myproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

/**
 * class use to expose our API to the outside world.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleOrderItemDTO {

    private Long id;
    private String productName;

    @Min(1)
    private int quantity;
}
