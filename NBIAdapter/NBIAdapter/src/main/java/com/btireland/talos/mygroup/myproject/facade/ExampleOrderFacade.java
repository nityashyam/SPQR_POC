package com.btireland.talos.mygroup.myproject.facade;

import com.btireland.talos.mygroup.myproject.domain.ExampleOrder;
import com.btireland.talos.mygroup.myproject.dto.ExampleOrderDTO;
import com.btireland.talos.mygroup.myproject.exception.ResourceNotFoundException;
import com.btireland.talos.mygroup.myproject.service.ExampleOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Component that interacts with the service layer and the REST layer.
 * Its role :
 * <ol>
 * <li>translate the API data model (DTO objects) into the persistence data model (entities)</li>
 * <li>Aggregate the calls to the service layer. So that the REST controller can call only one method from the facade.
 * That way, we are sure the REST controller deals with HTTP stuff only. The Facade orchestrates the business. The business is done by the service</li>
 * </ol>
 */
@Component
@Transactional
public class ExampleOrderFacade {

    private ExampleOrderMapper mapper;
    private ExampleOrderService exampleOrderService;

    public ExampleOrderFacade(ExampleOrderMapper mapper, ExampleOrderService exampleOrderService){
        this.mapper = mapper;
        this.exampleOrderService = exampleOrderService;
    }

    /**
     * Transform the DTO into an {@link com.btireland.talos.mygroup.myproject.domain.ExampleOrder} entity.
     * Create the entity and transform it back to DTO.
     * @param orderDTO example order to create
     * @return example order created
     */
    public ExampleOrderDTO create(ExampleOrderDTO orderDTO){
        ExampleOrder order = mapper.toExampleOrder(orderDTO);
        return mapper.toExampleOrderDTO(exampleOrderService.create(order));
    }

    /**
     * @param id id of an {@link ExampleOrder}
     * @return The {@link ExampleOrder} matching the given id as a DTO object
     * @throws ResourceNotFoundException if the example order with the given id can not be found
     */
    public ExampleOrderDTO findById(Long id){
        ExampleOrder order = exampleOrderService.findById(id);
        if (order == null){
            throw new ResourceNotFoundException("No order found with id "+id);
        }
        return mapper.toExampleOrderDTO(order);
    }

    /**
     *
     * @param id id of an {@link ExampleOrder}
     * @param orderDTO contains the data to update the order
     * @return The {@link ExampleOrder} after update as a DTO object.
     * @throws ResourceNotFoundException if the example order with the given id can not be found
     */
    public ExampleOrderDTO update(Long id, ExampleOrderDTO orderDTO){
        ExampleOrder order = exampleOrderService.findById(id);
        if (order == null){
            throw new ResourceNotFoundException("No order found with id "+id);
        }
        mapper.toExampleOrder(orderDTO, order);
        return mapper.toExampleOrderDTO(order);
    }

    public void delete(Long id){
        exampleOrderService.deleteById(id);
    }

    public Page<ExampleOrderDTO> findAll(@PathVariable String filters, Pageable pageable){
        Page<ExampleOrder> orders = exampleOrderService.findByFilters(filters, pageable);
        return orders.map(mapper::toExampleOrderDTO);
    }
}
