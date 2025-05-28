package com.example.demo.response;

import com.example.demo.dto.UserProfileData;
import com.example.demo.entity.Event;

import java.sql.Date;
import java.util.List;

public class JwtResponse{
    private String token;
    private String type = "Bearer";
    private Long id;
    private String registrationNumber;
    private String email;
    private String firstName;
    private String lastName;
    private String cnp;
    private Date dateOfBirth;
    private String university;
    private String faculty;
    private List<String> roles;
    private String responseMessage;
    private Object data;
    private List<Event> events;

    public JwtResponse(String token, Long id, String registrationNumber, List<String> roles){
        this.token = token;
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.roles = roles;
    }

    public JwtResponse(List<Event> events){
        this.events = events;
    }

    public JwtResponse(String message, Object data){
        this.responseMessage = message;
        this.data = data;
    }

    public JwtResponse(UserProfileData profileData) {
        this.token = profileData.token();
        this.registrationNumber = profileData.registrationNumber();
        this.email = profileData.email();
        this.firstName = profileData.firstName();
        this.lastName = profileData.lastName();
        this.cnp = profileData.cnp();
        this.dateOfBirth = profileData.dateOfBirth();
        this.university = profileData.university();
        this.faculty = profileData.faculty();
        this.roles = profileData.roles();
    }

    public JwtResponse(String message){
        this.responseMessage = message;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getToken() {
        return token;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getUniversity() {
        return university;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getCnp() {
        return cnp;
    }

    public Date getDateOfBirth(){ return dateOfBirth; }


    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public List<String> getRoles() {
        return roles;
    }
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setResponseMessage(String message) {
        this.responseMessage = message;
    }

    public void setEvents(List<Event> events){
        this.events = events;
    }

    public List<Event> getEvents(){
        return this.events;
    }
}
