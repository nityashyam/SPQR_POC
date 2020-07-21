package com.btireland.talos.mygroup.myproject.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER, faultStringOrReason = "Error processing client request")
public class ServerSoapFaultException extends SoapFaultException {

    public ServerSoapFaultException(String code, String message) {
        super(code, message);
    }

    public ServerSoapFaultException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
