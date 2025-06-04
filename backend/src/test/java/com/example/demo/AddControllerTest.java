package com.example.demo;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.controller.AddController;
import com.example.demo.entity.Role;
import com.example.demo.entity.Secretary;
import com.example.demo.entity.User;
import com.example.demo.model.enums.ERole;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.SecretaryRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.service.ValidationService;
import com.example.demo.util.AESUtil;
import com.example.demo.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(AddController.class)
@ImportAutoConfiguration(SecurityFilterAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class AddControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRepository studentRepository;
    @MockitoBean
    private SecretaryRepository secretaryRepository;
    @MockitoBean
    private AdminRepository adminRepository;

    @MockitoBean
    private ValidationService validationService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        // Always validate JWT by default
        given(jwtUtil.validateJwtToken(anyString())).willReturn(true);
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void addStudentSuccess() throws Exception {
        // Arrange: The key insight is that the controller calls:
        // 1. jwtUtil.getRegistrationNumberFromJwtToken(token) -> returns encrypted secretary reg number
        // 2. userDetailsService.loadUserByUsername(encryptedRegNumber) -> should find the user

        // Mock JWT extraction - this returns the decrypted secretary auth key that can be used to find the user
        given(jwtUtil.getRegistrationNumberFromJwtToken("valid-token"))
                .willReturn("secretaryAuthKey");  // This should be the key that finds the user

        // Create a secretary user with ROLE_SECRETARY
        User secretaryUser = new User();
        secretaryUser.setRoles(Set.of(new Role(ERole.ROLE_SECRETARY)));
        UserDetailsImpl userDetails = UserDetailsImpl.build(secretaryUser);

        // Mock userDetailsService to return the secretary user when called with the auth key
        given(userDetailsService.loadUserByUsername("secretaryAuthKey"))
                .willReturn(userDetails);

        // Mock validation checks
        given(validationService.isEmailUsed("student@example.com"))
                .willReturn(false);
        given(validationService.isAuthKeyUsed("S123"))
                .willReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/add/student")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "registrationNumber": "S123",
                                  "email": "student@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(ValidationMessage.STUDENT_ADDED)));
    }

    @Test
    void addStudentEmailInUse() throws Exception {
        // Mock JWT extraction
        given(jwtUtil.getRegistrationNumberFromJwtToken("token2"))
                .willReturn("secretaryAuthKey2");

        // Create secretary user
        User secretaryUser = new User();
        secretaryUser.setRoles(Set.of(new Role(ERole.ROLE_SECRETARY)));
        UserDetailsImpl userDetails = UserDetailsImpl.build(secretaryUser);

        given(userDetailsService.loadUserByUsername("secretaryAuthKey2"))
                .willReturn(userDetails);

        // Simulate duplicate email
        given(validationService.isEmailUsed("dup@example.com"))
                .willReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/add/student")
                        .header("Authorization", "Bearer token2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "registrationNumber": "S456",
                                  "email": "dup@example.com"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(ErrorMessage.EMAIL_IN_USE)));
    }

    @Test
    void addStudentAccessForbidden() throws Exception {
        // Mock JWT extraction
        given(jwtUtil.getRegistrationNumberFromJwtToken("invalid-role-token"))
                .willReturn("studentAuthKey");

        // Create user with ROLE_STUDENT (not ROLE_SECRETARY)
        User studentUser = new User();
        studentUser.setRoles(Set.of(new Role(ERole.ROLE_STUDENT)));
        UserDetailsImpl userDetails = UserDetailsImpl.build(studentUser);

        given(userDetailsService.loadUserByUsername("studentAuthKey"))
                .willReturn(userDetails);

        // Act & Assert
        mockMvc.perform(post("/api/add/student")
                        .header("Authorization", "Bearer invalid-role-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "registrationNumber": "S789",
                                  "email": "test@example.com"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(ErrorMessage.ACCESS_FORBIDDEN)));
    }

    @Test
    void addStudentInvalidToken() throws Exception {
        // Mock invalid JWT
        given(jwtUtil.validateJwtToken("invalid-token"))
                .willReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/add/student")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "registrationNumber": "S999",
                                  "email": "test@example.com"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(ErrorMessage.INVALID_DATA)));
    }
}