package com.example.demo.exceptions;

public class EmailSendingException extends Exception {
  public EmailSendingException(String message) {
    super(message);
  }
}