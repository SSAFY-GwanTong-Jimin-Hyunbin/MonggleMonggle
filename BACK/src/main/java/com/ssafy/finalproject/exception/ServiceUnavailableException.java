package com.ssafy.finalproject.exception;

public class ServiceUnavailableException extends CustomException {
    public ServiceUnavailableException(String message) {
        super("SERVICE_UNAVAILABLE", message);
    }
}

