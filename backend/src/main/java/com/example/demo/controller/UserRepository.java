package com.example.demo.controller;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.controller.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdNumber(String idNumber);

    Boolean existsByIdNumber(String idNumber);

    Boolean existsByEmail(String email);
}
