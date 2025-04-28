package com.example.demo.controller;

import com.example.demo.constants.ValidationGroup;
import com.example.demo.model.enums.ERole;
import com.example.demo.modelDB.Admin;
import com.example.demo.modelDB.Role;
import com.example.demo.modelDB.Secretary;
import com.example.demo.modelDB.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.SecretaryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.example.demo.util.AESUtil.decrypt;
import static com.example.demo.util.AESUtil.encrypt;

@RestController
@RequestMapping("/api/auth-admin")
public class AdminAuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecretaryRepository secretaryRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/register-secretary")
    public ResponseEntity<?> registerSecretary(@Validated(ValidationGroup.SecretaryGroup.class) @RequestBody RegisterRequest registerRequest){

        if(validateSecretaryCredentials(registerRequest)){
            User newUser = new User(
                    registerRequest.getFirstName(),
                    registerRequest.getLastName(),
                    encryptNoException(registerRequest.getRegistrationNumber()),
                    registerRequest.getUniversity(),
                    registerRequest.getFaculty(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    null,
                    registerRequest.getCnp()
            );
            Optional<Role> secretaryRole = roleRepository.findByName(ERole.ROLE_SECRETARY);
            Set<Role> roles = new HashSet<>();
            roles.add(secretaryRole.get());
            newUser.setRoles(roles);
            userRepository.save(newUser);
            return ResponseEntity.ok("Account created successfully");
        }
        else
            return ResponseEntity.status(400).body("Data provided is wrong!");
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@Validated(ValidationGroup.AdminGroup.class) @RequestBody RegisterRequest registerRequest)
    {
        if(validateAdminCredentials(registerRequest)){
            User newUser = new User();
            newUser.setFirstName(registerRequest.getFirstName());
            newUser.setLastName(registerRequest.getLastName());
            newUser.setRegNumber(encryptNoException(registerRequest.getRegistrationNumber()));
            newUser.setUniversity(registerRequest.getUniversity());
            newUser.setFaculty(registerRequest.getFaculty());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPassword(registerRequest.getPassword());
            newUser.setDateOfBirth(null);
            newUser.setCnp(registerRequest.getCnp());
            Optional<Role> secretaryRole = roleRepository.findByName(ERole.ROLE_ADMIN);
            Set<Role> roles = new HashSet<>();
            roles.add(secretaryRole.get());
            newUser.setRoles(roles);
            userRepository.save(newUser);
            return ResponseEntity.ok("Account created successfully");
        }
        else
            return ResponseEntity.status(400).body("Data provided is wrong!");
    }

    private static String encryptNoException(String input){
        try{
            return encrypt(input);
        }catch(Exception ex)
        {
            System.err.println(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private static String decryptNoException(String input){
        try{
            return decrypt(input);
        }catch(Exception ex)
        {
            System.err.println(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private boolean validateSecretaryCredentials(RegisterRequest registerRequest){
            if (secretaryRepository.existsByCNP(registerRequest.getCnp())){
                Secretary newSecretary = secretaryRepository.findByCNP(registerRequest.getCnp()).get();

                String firstNameRegister = registerRequest.getFirstName().toLowerCase().trim();
                String lastNameRegister = registerRequest.getLastName().toLowerCase().trim();
                String authKeyRegister = registerRequest.getRegistrationNumber();

                String firstNameDB = newSecretary.getFirstName().toLowerCase().trim();
                String lastNameDB = newSecretary.getLastName().toLowerCase().trim();
                String authKeyDB = decryptNoException(newSecretary.getAuthKey());

                return firstNameRegister.equals(firstNameDB) &&
                        lastNameRegister.equals(lastNameDB) &&
                        authKeyRegister.equals(authKeyDB);
            }
            else
                return false;
    }

    private boolean validateAdminCredentials(RegisterRequest registerRequest){
        if(adminRepository.existsByEmail(registerRequest.getEmail())){
            Admin newAdmin = adminRepository.findByEmail(registerRequest.getEmail()).get();

            String authKeyRegister = registerRequest.getRegistrationNumber();

            String authKeyDB = decryptNoException(newAdmin.getAuthKey());

            return authKeyRegister.equals(authKeyDB);
        }
        else
            return false;
    }

}
