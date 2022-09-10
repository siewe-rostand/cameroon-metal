package com.siewe.pos;

public class InvalidOrderItemException extends Exception {
    public InvalidOrderItemException(String message) {
        super(message);
    }
}
