package com.btireland.talos.mygroup.myproject.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class SoapFaultException extends Exception {

    @Getter
    private final String code;

    @Getter
    private final List<String> detailedMessages;

    public SoapFaultException(String code, String message) {
        super(message);
        this.code = code;
        this.detailedMessages = new ArrayList<>();
    }

    public SoapFaultException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.detailedMessages = new ArrayList<>();
    }

    public void addDetailMessage(String msg){
        detailedMessages.add(msg);
    }
    public void addDetailMessage(List<String> msgs){
        detailedMessages.addAll(msgs);
    }
}
