package com.example.demo.repository;

import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByRegNumber(String regNumber);

    Boolean existsByRegNumber(String regNumber);

    Boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.regNumber = :reg")
    void deleteByRegNumber(@Param("reg") String reg);
}
