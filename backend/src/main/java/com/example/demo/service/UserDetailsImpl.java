package com.example.demo.service;

import com.example.demo.modelDB.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String registrationNumber;
    private String email;
    private String firstName;
    private String lastName;
    private String cnp;
    private Date dateOfBirth;
    private String university;
    private String faculty;
    private Collection<? extends GrantedAuthority> authorities;

    @JsonIgnore
    private String password;

    public UserDetailsImpl(
            Long id,
            String registrationNumber,
            String email, String password,
            Collection<? extends GrantedAuthority> authorities,
            String firstName,
            String lastName,
            String cnp,
            Date dateOfBirth,
            String university,
            String faculty){
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cnp = cnp;
        this.dateOfBirth = dateOfBirth;
        this.university = university;
        this.faculty = faculty;
    }

    public static UserDetailsImpl build(User user){
        List<GrantedAuthority> authorityList = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

        return new UserDetailsImpl(user.getId(), user.getRegNumber(), user.getEmail(), user.getPassword(), authorityList, user.getFirstName(), user.getLastName(), user.getCnp(), user.getDateOfBirth(), user.getUniversity(), user.getFaculty());

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities;
    }

    public String getCnp() {
        return cnp;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getUniversity() {
        return university;
    }

    public String getFaculty() {
        return faculty;
    }

    public Long getId(){
        return id;
    }

    public String getEmail(){
        return email;
    }

    @Override
    public String getPassword(){
        return password;
    }

    @Override
    public String getUsername(){
        return registrationNumber;
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return true;
    }

    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }



}
