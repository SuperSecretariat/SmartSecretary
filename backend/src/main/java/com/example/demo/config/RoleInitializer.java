package com.example.demo.config;

import com.example.demo.model.enums.ERole;
import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {
    private static final Logger loggerRoleInitializer = LoggerFactory.getLogger(RoleInitializer.class);
    private final RoleRepository roleRepository;

    @Autowired
    public RoleInitializer(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        loggerRoleInitializer.info("Running RoleInitializer...");

        for (ERole role : ERole.values()) {
            boolean exists = roleRepository.findByName(role).isPresent();
            if (!exists) {
                roleRepository.save(new Role(role));
                loggerRoleInitializer.info("Saved missing role: {}", role);
            } else {
                loggerRoleInitializer.info("Role already exists: {}", role);
            }
        }
    }
}
