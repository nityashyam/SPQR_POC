package com.btireland.talos.prequaladapter.rest;

import com.btireland.talos.prequaladapter.soap.SPQRRequest;

public class SampleReq {
    String name;
    Order order;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
