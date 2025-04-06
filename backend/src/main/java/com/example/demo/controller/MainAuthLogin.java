package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainAuthLogin {
    public static void main(String[] args) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Select operation: login/register");
        String command = reader.readLine();
        switch(command){
            case "login": {
                try{
                    System.out.println("Introdu numarul matricol: ");
                    String idNumber = reader.readLine();

                    System.out.println("Introdu parola: ");
                    String password = reader.readLine();
                }
                catch (IOException ex)
                {
                    System.err.println(ex.getMessage());
                }
                break;
            }
            case "register":{
                try{
                    System.out.println("Introdu numele de familie: ");
                    String firstName = reader.readLine();

                    System.out.println("Introdu prenumele: ");
                    String lastName = reader.readLine();

                    System.out.println("Introdu numarul matricol: ");
                    String idNumber = reader.readLine();

                    System.out.println("Introdu emailul: ");
                    String email = reader.readLine();

                    System.out.println("Introdu parola");
                    String password = reader.readLine();

                    System.out.println("Re-confirma parola");
                    String confirmationPassword = reader.readLine();

                    Register newRegisterRequest = new Register(firstName, lastName, idNumber, email, password, confirmationPassword);
                }
                catch (IOException ex)
                {
                    System.err.println(ex.getMessage());
                }
            }
        }

    }

}
