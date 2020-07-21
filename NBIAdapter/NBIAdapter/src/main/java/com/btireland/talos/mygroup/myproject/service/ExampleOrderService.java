package com.btireland.talos.mygroup.myproject.service;

import com.btireland.talos.mygroup.myproject.domain.ExampleOrder;
import com.btireland.talos.mygroup.myproject.repository.ExampleOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExampleOrderService {

    private ExampleOrderRepository exampleOrderRepository;

    public ExampleOrderService(ExampleOrderRepository exampleOrderRepository) {
        this.exampleOrderRepository = exampleOrderRepository;
    }

    public ExampleOrder create(ExampleOrder order){
        return exampleOrderRepository.save(order);
    }

    public ExampleOrder findById(Long id){
        return exampleOrderRepository.findById(id).orElse(null);
    }

    public ExampleOrder update(ExampleOrder order){
        return exampleOrderRepository.save(order);
    }

    public void deleteById(Long id){
        exampleOrderRepository.deleteById(id);
    }

    /**
     * Use RSQL framework to parse a String of filters and transform it into a J
     * @param filters
     * @return
     */
    public Page<ExampleOrder> findByFilters(String filters, Pageable pageable){
        return exampleOrderRepository.findAllByRsqlQuery(filters, pageable);
    }
}
