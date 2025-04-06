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
        verifyIncompleteData();
        verifyPasswords();
    }

    private void verifyIncompleteData()
    {
        try{
            if(firstName == null || lastName == null || idNumber == null || email == null || password == null || confirmationPassword == null)
            {
                throw new IncompleteDataException("Trebuie sa completezi toate campurile");
            }
        }
        catch(IncompleteDataException ex)
        {
            System.err.println(ex.getMessage());
        }
    }

    private void verifyPasswords()
    {
        try{
            if(!this.password.equals(this.confirmationPassword))
                throw new PasswordNotMatchingException("Parolele nu coincid!");
            else
            {
                //tempId e un place-holder pentru cand avem o metoda sa aflam cate conturi exista in sistem
                Long tempId = (long) 0;
                Student newStudentAccount = new Student(tempId, this.firstName, this.lastName, this.idNumber, this.email, this.password);
                //adaugam student-ul in baza de date
            }
        }catch(PasswordNotMatchingException ex)
        {
            System.err.println(ex.getMessage());
        }

    }
}
