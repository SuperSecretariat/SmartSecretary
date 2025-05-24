package com.example.demo.service;

import com.example.demo.dto.UserDetailsData;
import com.example.demo.entity.User;
import com.example.demo.model.enums.ERole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
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

    public UserDetailsImpl(UserDetailsData data) {
        this.id = data.id();
        this.registrationNumber = data.registrationNumber();
        this.password = data.password();
        this.email = data.email();
        this.authorities = data.authorities();
        this.firstName = data.firstName();
        this.lastName = data.lastName();
        this.cnp = data.cnp();
        this.dateOfBirth = data.dateOfBirth();
        this.university = data.university();
        this.faculty = data.faculty();
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorityList = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        UserDetailsData data = new UserDetailsData(
                user.getId(),
                user.getRegNumber(),
                user.getEmail(),
                user.getPassword(),
                authorityList,
                user.getFirstName(),
                user.getLastName(),
                user.getCnp(),
                user.getDateOfBirth(),
                user.getUniversity(),
                user.getFaculty()
        );

        return new UserDetailsImpl(data);
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

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public List<String> getRoleNames() {
        return this.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    public boolean hasRole(ERole role) {
        return getRoleNames().contains(role.name());
    }


}
