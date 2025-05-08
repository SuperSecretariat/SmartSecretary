package com.example.demo.repository;

import com.example.demo.entity.Secretary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecretaryRepository extends JpaRepository<Secretary, Long> {
    Optional<Secretary> findByAuthKey(String authKey);
    boolean existsByAuthKey(String authKey);
}
