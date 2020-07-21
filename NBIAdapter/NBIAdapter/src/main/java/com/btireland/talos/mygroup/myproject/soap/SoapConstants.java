package com.btireland.talos.mygroup.myproject.soap;

import javax.xml.namespace.QName;

public class SoapConstants {

    public static final String TALOS_NAMESPACE_URI = "http://talos.btireland.com/ws/schemas/v1";
    public static final QName FAULT_DETAIL_MAIN_ELEMENT_NAME = new QName(TALOS_NAMESPACE_URI, "CODE", "talos-ws");
    public static final QName FAULT_DETAIL_ELEMENT_NAME = new QName(TALOS_NAMESPACE_URI, "MESSAGE", "talos-ws");

    public static final String FAULT_VALIDATION_ERROR_CODE = "400";
    public static final String FAULT_VALIDATION_ERROR_MESSAGE = "Client request invalid";

    private SoapConstants(){

    }
}
