package com.example.demo.controller;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.controller.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById_number(String id_number);

    Boolean existsById_number(String id_number);

    Boolean existsByEmail(String email);
}
