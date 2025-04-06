package com.example.demo.controller;

public class IncompleteDataException extends RuntimeException {
    public IncompleteDataException(String message) {
        super(message);
    }
}
