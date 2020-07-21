package com.btireland.talos.mygroup.myproject.util;

import com.btireland.talos.mygroup.myproject.dto.ExampleOrderDTO;
import com.btireland.talos.mygroup.myproject.dto.ExampleOrderItemDTO;

import java.time.LocalDateTime;
import java.util.Arrays;

public class ExampleOrderDTOFactory {

    public static ExampleOrderDTO defaultExampleOrderRequest(){
        return ExampleOrderDTO.builder().externalReference("100").type("PFIB")
                .items(Arrays.asList(ExampleOrderItemDTO.builder().productName("FTTH").quantity(1).build()))
                .build();
    }
    public static ExampleOrderDTO defaultExampleOrderResponse(){
        return ExampleOrderDTO.builder().id(1L).externalReference("100").type("PFIB").createdAt(LocalDateTime.parse("2020-01-16T10:30:37"))
                .items(Arrays.asList(ExampleOrderItemDTO.builder().id(1L).productName("FTTH").quantity(1).build()))
                .build();
    }
}
