package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByRegNumber(String regNumber);
    Optional<User> findByEmail(String email);
    Boolean existsByRegNumber(String regNumber);
    Boolean existsByEmail(String email);
}
