package com.example.demo.config;

import com.example.demo.model.enums.ERole;
import com.example.demo.modelDB.Role;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        System.out.println("Running RoleInitializer...");

        for (ERole role : ERole.values()) {
            boolean exists = roleRepository.findByName(role).isPresent();
            if (!exists) {
                roleRepository.save(new Role(role));
                System.out.println("Saved missing role: " + role);
            } else {
                System.out.println("Role already exists: " + role);
            }
        }
    }
}
