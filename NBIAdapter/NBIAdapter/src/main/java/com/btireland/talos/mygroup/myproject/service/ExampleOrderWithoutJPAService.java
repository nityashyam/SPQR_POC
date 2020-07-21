package com.btireland.talos.mygroup.myproject.service;

import com.btireland.talos.mygroup.myproject.dto.ExampleOrderDTO;
import com.btireland.talos.mygroup.myproject.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service that does nothing, just keeping in memory everything and printing out.
 * For DEMO purpose
 */
@Slf4j
@Service
@Transactional
public class ExampleOrderWithoutJPAService {

    private AtomicLong orderSequence;
    private Map<Long, ExampleOrderDTO> transferedOrders;

    public ExampleOrderWithoutJPAService() {
        orderSequence = new AtomicLong(0);
        transferedOrders = new HashMap<>();
    }

    private Long getNextOrderId(){
        return orderSequence.incrementAndGet();
    }

    public ExampleOrderDTO transferOrder(ExampleOrderDTO order){
        order.setId(getNextOrderId());
        transferedOrders.put(order.getId(), order);
        log.info("Order {} has been transfered to myAwesomeOrderService", order.getId());
        return order;
    }

    public ExampleOrderDTO findById(Long id){
        ExampleOrderDTO order = transferedOrders.get(id);
        if (order == null){
            throw new ResourceNotFoundException("No order found with id "+id);
        }
        return order;
    }

    public ExampleOrderDTO notifyChange(Long id, ExampleOrderDTO newOrder){
        ExampleOrderDTO order = findById(id);
        transferedOrders.put(order.getId(), newOrder);
        log.info("Order {} has changed {}", order.getId(), newOrder);
        return newOrder;
    }

}
