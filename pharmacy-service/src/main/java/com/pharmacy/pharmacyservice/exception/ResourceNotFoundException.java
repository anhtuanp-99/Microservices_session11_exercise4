package com.pharmacy.pharmacyservice.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s với %s %s không tồn tại trên hệ thống!",
                resourceName, fieldName, fieldValue));
    }
}