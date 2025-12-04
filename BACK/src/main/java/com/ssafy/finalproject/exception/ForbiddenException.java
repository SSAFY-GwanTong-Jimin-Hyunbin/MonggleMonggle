package com.ssafy.finalproject.exception;

public class ForbiddenException extends CustomException {
    public ForbiddenException(String message) {
        super("FORBIDDEN", message);
    }
}

