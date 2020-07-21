package com.btireland.talos.mygroup.myproject.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        this("EntityRepresentationModel not found!");
    }

    public ResourceNotFoundException(String message) {
        this(message, null);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
