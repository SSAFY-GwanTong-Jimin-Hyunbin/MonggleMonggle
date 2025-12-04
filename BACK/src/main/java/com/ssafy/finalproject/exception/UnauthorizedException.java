package com.ssafy.finalproject.exception;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }
}

