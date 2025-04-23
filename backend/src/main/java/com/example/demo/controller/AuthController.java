package com.example.demo.controller;

import com.example.demo.model.enums.ERole;
import com.example.demo.modelDB.Role;
import com.example.demo.modelDB.Student;
import com.example.demo.modelDB.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.RegisterRequest;
import com.example.demo.response.JwtResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByRegNumber(loginRequest.getRegistrationNumber());
        Set<Role> userRole = optionalUser.get().getRoles();

        List<String> roleNames = userRole.stream().map(role -> role.getName().toString()).toList();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(loginRequest.getPassword()))
                return ResponseEntity.ok(new JwtResponse("placeholder-token", user.getId(), user.getIdNumber(), roleNames));
            else
                return ResponseEntity.status(401).body("Parola incorecta");
        } else {
            return ResponseEntity.status(404).body("Contul nu exista");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByRegNumber(registerRequest.getRegistrationNumber());

        if (optionalUser.isPresent()) {
            return ResponseEntity.status(409).body("Un cont cu acelasi numar matricol a fost creat deja");
        }

        if(validateUserCredentials(registerRequest))
        {
            User newUser = new User(
                    registerRequest.getLastName(),
                    registerRequest.getFirstName(),
                    registerRequest.getRegistrationNumber(),
                    registerRequest.getUniversity(),
                    registerRequest.getFaculty(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getDateOfBirth(),
                    registerRequest.getCNP()
            );
            Optional<Role> studentRole = roleRepository.findByName(ERole.ROLE_STUDENT);
            Set<Role> roles = new HashSet<>();
            roles.add(studentRole.get());
            newUser.setRoles(roles);
            userRepository.save(newUser);
            return ResponseEntity.ok("Cont creat cu succes!");
        }
        else {
            return ResponseEntity.status(400).body("Data provided is wrong!");
        }


    }

    private boolean validateUserCredentials(RegisterRequest registerRequest){
        if(!studentRepository.existsByRegNumber(registerRequest.getRegistrationNumber()))
            return false;
        else {
            Student studentData = studentRepository.findByRegNumber(registerRequest.getRegistrationNumber()).get();
            String firstNameRegister = registerRequest.getFirstName().toLowerCase();
            String lastNameRegister = registerRequest.getLastName().toLowerCase();
            LocalDate dateOfBirthRegister = registerRequest.getDateOfBirth().toLocalDate();
            String CNPRegister = registerRequest.getCNP();

            String firstNameStudent = studentData.getFirstName().toLowerCase();
            String lastNameStudent = studentData.getLastName().toLowerCase();
            LocalDate dateOfBirthStudent = studentData.getDateOfBirth().toLocalDate();
            String CNPStudent = studentData.getCNP();

            if(!firstNameRegister.equals(firstNameStudent))
                return false;
            else if(!lastNameRegister.equals(lastNameStudent))
                return false;
            else if(!dateOfBirthRegister.equals(dateOfBirthStudent))
                return false;
            else if(!CNPRegister.equals(CNPStudent))
                return false;

            return true;
        }
    }

}
