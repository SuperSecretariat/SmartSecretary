package com.example.demo.exceptions;

/**
 * Exception thrown when the number of received fields is
 * different from the number of fields in the form.
 */
public class FormRequestFieldDataException extends Exception {
    public FormRequestFieldDataException(String message) {
        super(message);
    }
}
