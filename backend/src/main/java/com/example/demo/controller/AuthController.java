package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.exceptions.DecryptionException;
import com.example.demo.exceptions.EmailSendingException;
import com.example.demo.exceptions.EncryptionException;
import com.example.demo.model.enums.ERole;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.request.ForgotPasswordRequest;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.RegisterRequest;
import com.example.demo.request.ResetPasswordRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.util.JwtUtil;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.demo.util.AESUtil.decrypt;
import static com.example.demo.util.AESUtil.encrypt;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final SecretaryRepository secretaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Logger loggerAuthController = LoggerFactory.getLogger(AuthController.class);
    private EmailService emailService;

    @Autowired
    public AuthController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            StudentRepository studentRepository,
            SecretaryRepository secretaryRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            EmailService emailService
    ){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.studentRepository = studentRepository;
        this.secretaryRepository = secretaryRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
            User currentUser = findUserByIdentifier(loginRequest.getRegistrationNumber());

            if (currentUser == null){
                return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
            }
            Set<Role> userRole = currentUser.getRoles();

            List<String> roleNames = userRole.stream().map(role -> role.getName().toString()).toList();
            if (passwordEncoder.matches(loginRequest.getPassword(), currentUser.getPassword())) {
                UserDetailsImpl userDetails = UserDetailsImpl.build(currentUser);

                String token = jwtUtil.generateJwtToken(
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                );
                return ResponseEntity.ok(new JwtResponse(token, currentUser.getId(), currentUser.getRegNumber(), roleNames));
            } else
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INCORRECT_PASSWORD));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest registerRequest) {
        ERole newAccountRole = null;
        String email = registerRequest.getEmail();
        if(userRepository.existsByEmail(email))
        {
            return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.AUTH_KEY_IN_USE));
        }
        else{

                String tempAuthKey = encryptException(registerRequest.getRegistrationNumber());
                if(studentRepository.existsByRegNumber(registerRequest.getRegistrationNumber()))
                    newAccountRole = ERole.ROLE_STUDENT;
                else if(secretaryRepository.existsByAuthKey(tempAuthKey))
                    newAccountRole = ERole.ROLE_SECRETARY;
                else if(adminRepository.existsByAuthKey(tempAuthKey))
                    newAccountRole = ERole.ROLE_ADMIN;
                else
                    return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        }
        if(validateData(registerRequest, newAccountRole)){
            Optional<Role> accountRole = roleRepository.findByName(newAccountRole);
            Set<Role> roles = new HashSet<>();
            roles.add(accountRole.get());
            String tempKey = registerRequest.getRegistrationNumber();
            String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
            if(newAccountRole.equals(ERole.ROLE_ADMIN) || newAccountRole.equals(ERole.ROLE_SECRETARY))
            {
                tempKey = encryptException(tempKey);
            }

            User newUser = new User(
                    registerRequest.getLastName(),
                    registerRequest.getFirstName(),
                    tempKey,
                    null,
                    null,
                    registerRequest.getEmail(),
                    hashedPassword,
                    null,
                    null
            );
            newUser.setRoles(roles);
            userRepository.save(newUser);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.ACCOUNT_SUCCESS));
        }
        else
            return ResponseEntity.status(400).body(new JwtResponse(ErrorMessage.INVALID_DATA));
    }

    @PostMapping("/logout")
    public ResponseEntity<JwtResponse> logout(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        String token = null;

        if(headerAuth != null && headerAuth.startsWith("Bearer ")){
            token = headerAuth.substring(7);

            jwtUtil.invalidateToken(token);

            return ResponseEntity.ok(new JwtResponse(ValidationMessage.LOGOUT_SUCCESS));
        }
        return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.UNKNOWN_ERROR));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<JwtResponse> forgotPassword(@RequestBody ForgotPasswordRequest request){
        String email = request.getEmail();
        Optional<User> temporaryUser = userRepository.findByEmail(email);
        if(temporaryUser.isEmpty()){
            return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
        }

        User user = temporaryUser.get();
        String resetPasswordToken = jwtUtil.generatePasswordResetToken(user.getEmail());
        String resetLink = "http://localhost:4200/reset-password?token=" + resetPasswordToken;

        String subject = "Reset your password";
        String content = "Link to reset your accounts password: " + resetLink;
        try{
            emailService.sendEmail(user.getEmail(), subject, content);
        } catch (EmailSendingException e) {
            loggerAuthController.error("Error while sending the email");
        }

        return ResponseEntity.ok(new JwtResponse(ValidationMessage.EMAIL_SUCCESS));

    }

    @PostMapping("/reset-password")
    public ResponseEntity<JwtResponse> resetPassword(@RequestBody ResetPasswordRequest request){
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        if(!jwtUtil.validateJwtToken(token)){
            return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        }

        String email = jwtUtil.getRegistrationNumberFromJwtToken(token);
        Optional<User> tempUser = userRepository.findByEmail(email);

        if(tempUser.isEmpty()){
            return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
        }

        User user = tempUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        jwtUtil.invalidateToken(token);

        return ResponseEntity.ok(new JwtResponse(ValidationMessage.UPDATE_SUCCESS));
    }

    @GetMapping("/me")
    public ResponseEntity<JwtResponse> getCurrentUser(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> rolesName = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

        JwtResponse response = new JwtResponse(null, userDetails.getId(), userDetails.getUsername(), rolesName);

        return ResponseEntity.ok(response);
    }

    private boolean validateData(RegisterRequest registerRequest, ERole registerRole){
        String tempAuthKey = encryptException(registerRequest.getRegistrationNumber());
        switch(registerRole){
            case ROLE_STUDENT:
                Student tempStudent = studentRepository.findByRegNumber(registerRequest.getRegistrationNumber()).get();
                return tempStudent.getRegNumber().equals(registerRequest.getRegistrationNumber()) && tempStudent.getEmail().equals(registerRequest.getEmail());
            case ROLE_SECRETARY:

                Secretary tempSecretary = secretaryRepository.findByAuthKey(tempAuthKey).get();
                    String tempSecretaryKey = decryptException(tempSecretary.getAuthKey());
                    return tempSecretaryKey.equals(registerRequest.getRegistrationNumber()) && tempSecretary.getEmail().equals(registerRequest.getEmail());


            case ROLE_ADMIN:
                Admin tempAdmin = adminRepository.findByAuthKey(tempAuthKey).get();
                    String tempAdminKey = decryptException(tempAdmin.getAuthKey());
                    return tempAdminKey.equals(registerRequest.getRegistrationNumber()) && tempAdmin.getEmail().equals(registerRequest.getEmail());
            default:
                return false;
        }
    }

    public User findUserByIdentifier(String identifier){
        String tempKey = encryptException(identifier);
        Optional<User> optionalUserCrypt = userRepository.findByRegNumber(tempKey);
        Optional<User> optionalUserStudent = userRepository.findByRegNumber(identifier);
        boolean isAdmin = true;
        Optional<User> emailUser = userRepository.findByEmail(identifier);
        if(emailUser.isPresent()){
            return emailUser.get();
        }
        if (optionalUserCrypt.isEmpty() && optionalUserStudent.isEmpty())
            return null;

        if(!optionalUserStudent.isEmpty()) {
            isAdmin = false;
        }

        if(isAdmin)
            return optionalUserCrypt.get();
        else
            return optionalUserStudent.get();
    }

    private static String encryptException(String input){
        try{
            return encrypt(input);
        } catch (EncryptionException ex){
            loggerAuthController.error(ex.getMessage());
            return "";
        }
    }

    private static String decryptException(String input) {
        try {
            return decrypt(input);
        } catch (DecryptionException ex){
            loggerAuthController.error(ex.getMessage());
            return "";
        }
    }


}
