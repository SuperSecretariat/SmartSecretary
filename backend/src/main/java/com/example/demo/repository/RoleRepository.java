package com.example.demo.repository;

import java.util.Optional;

import com.example.demo.entity.Role;
import com.example.demo.model.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
