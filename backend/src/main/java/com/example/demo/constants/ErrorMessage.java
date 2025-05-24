package com.example.demo.constants;

public class ErrorMessage {
    public static final String MISSING_PASSWORD = "Password is missing";
    public static final String MISSING_CONFIRMATION_PASSWORD = "Confirmation password is missing";
    public static final String MISSING_EMAIL = "Email is missing";
    public static final String MISSING_REGISTRATION_NUMBER = "Registration number is missing";
    public static final String MISSING_LAST_NAME = "Last name is missing";
    public static final String MISSING_FIRST_NAME = "First name is missing";
    public static final String MISSING_UNIVERSITY = "University is missing";
    public static final String MISSING_FACULTY = "Faculty is missing";
    public static final String MISSING_DATE_OF_BIRTH = "Date of birth is missing";
    public static final String MISSING_CNP = "CNP is missing";
    public static final String MISSING_AUTHKEY = "AuthKey is missing";
    public static final String REG_NUMBER_IN_USE = "Registration number already in use";
    public static final String AUTH_KEY_IN_USE = "Authentication key already in use";
    public static final String UNKNOWN_ERROR = "An unknown error has occurred";
    public static final String NON_EXISTENT_USER = "User doesn't exist";
    public static final String INCORRECT_PASSWORD = "The password is incorrect";
    public static final String INVALID_DATA = "Data provided is invalid";
    public static final String EMAIL_IN_USE = "The email provided is already in use";
    public static final String ACCESS_FORBIDDEN = "This account doesn't have the privilege to do this";
    public static final String DECRYPTION_ERROR = "An error occurred during decryption";
    public static final String ENCRYPTION_ERROR = "An error occurred during encryption";

    //Form & FormRequest error messages
    public static final String MISSING_FORM_TITLE = "Title is missing or empty";
    public static final String MISSING_JWT_TOKEN = "JWT token is missing or empty";
    public static final String MISSING_FORM_ID = "Form id is missing or empty";
    public static final String MISSING_NUMBER_OF_INPUT_FIELDS = "Number of Input Fields is missing or empty";

    private ErrorMessage(){}
}
