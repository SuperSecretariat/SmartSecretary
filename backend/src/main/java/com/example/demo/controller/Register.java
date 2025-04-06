package com.example.demo.controller;

public class Register {
    private String firstName;
    private String lastName;
    private String idNumber;
    private String email;
    private String password;
    private String confirmationPassword;

    public Register(String firstName, String lastName, String idNumber, String email, String password, String confirmationPassword)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.idNumber = idNumber;
        this.email = email;
        this.password = password;
        this.confirmationPassword = confirmationPassword;
    }

    private void verifyIncompleteData()
    {
        try{
            if(firstName == null || lastName == null || idNumber == null || email == null || password == null || confirmationPassword == null)
            {
                throw new IncompleteDataException("You need to complete all the fields!");
            }
        }
        catch(IncompleteDataException ex)
        {
            System.err.println(ex.getMessage());
        }
    }
}
