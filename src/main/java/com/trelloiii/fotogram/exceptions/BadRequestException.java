package com.trelloiii.fotogram.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException() {
        super("Bad request");
    }
}
