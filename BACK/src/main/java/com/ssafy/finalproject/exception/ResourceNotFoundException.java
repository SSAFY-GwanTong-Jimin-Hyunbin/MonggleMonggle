package com.ssafy.finalproject.exception;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(String message) {
        super("NOT_FOUND", message);
    }
}

