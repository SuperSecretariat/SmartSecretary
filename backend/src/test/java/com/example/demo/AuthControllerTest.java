package com.example.demo;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.controller.AuthController;
import com.example.demo.entity.User;
import com.example.demo.model.enums.ERole;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.TokenBlacklistService;
import com.example.demo.service.ValidationService;
import com.example.demo.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ValidationService validationService;
    @Mock
    private EmailService emailService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenBlacklistService tokenBlacklistService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthController authController;

    private User studentUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();


        studentUser = new User();
        studentUser.setId(1L);
        studentUser.setRegNumber("S123");
        studentUser.setEmail("student@example.com");
        studentUser.setPassword("encodedPass");
        studentUser.setRoles(Set.of(new com.example.demo.entity.Role(ERole.ROLE_STUDENT)));
    }

    @Test
    void login_success_returnsToken() throws Exception {
        when(validationService.findUserByIdentifier("S123")).thenReturn(studentUser);
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateJwtToken(any())).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"registrationNumber\":\"S123\",\"password\":\"rawPass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void login_userNotFound_returns404() throws Exception {
        when(validationService.findUserByIdentifier(anyString())).thenReturn(null);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"registrationNumber\":\"X\",\"password\":\"p\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.responseMessage").value(ErrorMessage.NON_EXISTENT_USER));
    }

    @Test
    void login_incorrectPassword_returns401() throws Exception {
        when(validationService.findUserByIdentifier("S123")).thenReturn(studentUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"registrationNumber\":\"S123\",\"password\":\"bad\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.responseMessage").value(ErrorMessage.INCORRECT_PASSWORD));
    }

    // TODO: write tests for /register, /logout, /forgot-password, /reset-password, /me
}
