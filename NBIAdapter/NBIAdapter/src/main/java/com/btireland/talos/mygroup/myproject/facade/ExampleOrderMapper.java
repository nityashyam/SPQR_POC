package com.btireland.talos.mygroup.myproject.facade;

import com.btireland.talos.mygroup.myproject.domain.ExampleOrder;
import com.btireland.talos.mygroup.myproject.domain.ExampleOrderItem;
import com.btireland.talos.mygroup.myproject.dto.ExampleOrderDTO;
import com.btireland.talos.mygroup.myproject.dto.ExampleOrderItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Map the entity ExampleOrder and its DTO
 */
@Mapper(componentModel = "spring")
public interface ExampleOrderMapper {

    ExampleOrderDTO toExampleOrderDTO(ExampleOrder order);

    ExampleOrder toExampleOrder(ExampleOrderDTO orderDTO);

    /**
     * copy orderDTO into order. order must already exist.
     * @param orderDTO DTO to copy from
     * @param order entity to copy into.
     */
    void toExampleOrder(ExampleOrderDTO orderDTO, @MappingTarget ExampleOrder order);

    ExampleOrderItemDTO toExampleOrderItemDTO(ExampleOrderItem item);

    ExampleOrderItem toExampleOrderItem(ExampleOrderItemDTO itemDTO);
}
