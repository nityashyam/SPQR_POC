package com.btireland.talos.mygroup.myproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * class use to expose our API to the outside world.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExampleOrderDTO {

    private Long id;

    @NotEmpty
    private String externalReference;
    private String type;
    private LocalDateTime createdAt;

    @NotEmpty
    @Valid
    private List<ExampleOrderItemDTO> items;

}
