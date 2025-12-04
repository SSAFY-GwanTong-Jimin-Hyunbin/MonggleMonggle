package com.ssafy.finalproject.exception;

public class ConflictException extends CustomException {
    public ConflictException(String message) {
        super("CONFLICT", message);
    }
}

