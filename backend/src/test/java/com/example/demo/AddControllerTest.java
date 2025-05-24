package com.example.demo;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.controller.AddController;
import com.example.demo.dto.StudentRequest;
import com.example.demo.entity.Secretary;
import com.example.demo.entity.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.SecretaryRepository;
import com.example.demo.repository.StudentRepository;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
    }

    @Test
    void addStudentSuccess() throws Exception {
        // Arrange: valid token & decryption flow
        given(jwtUtil.getRegistrationNumberFromJwtToken("valid-token"))
                .willReturn("encryptedKey");

        try (MockedStatic<AESUtil> aes = Mockito.mockStatic(AESUtil.class)) {
            // decrypt token
            aes.when(() -> AESUtil.decrypt("encryptedKey"))
                    .thenReturn("secKey");

            // stub secretary lookup
            Secretary sec = new Secretary("secKey", "sec@example.com");
            given(validationService.findSecretary("secKey"))
                    .willReturn(sec);

            // decrypt secretary authKey
            aes.when(() -> AESUtil.decrypt(sec.getAuthKey()))
                    .thenReturn("userKey");

            // return a real user instance
            User dummyUser = new User();
            given(validationService.findUserByIdentifier("userKey"))
                    .willReturn(dummyUser);

            // email and reg-number free
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
    }

    @Test
    void addStudentEmailInUse() throws Exception {
        // Arrange: token & flow
        given(jwtUtil.getRegistrationNumberFromJwtToken("token2"))
                .willReturn("key2");

        try (MockedStatic<AESUtil> aes = Mockito.mockStatic(AESUtil.class)) {
            aes.when(() -> AESUtil.decrypt("key2"))
                    .thenReturn("secKey2");

            Secretary sec = new Secretary("enc2", "sec2@example.com");
            given(validationService.findSecretary("secKey2"))
                    .willReturn(sec);

            aes.when(() -> AESUtil.decrypt(sec.getAuthKey()))
                    .thenReturn("userKey2");

            User dummyUser = new User();
            given(validationService.findUserByIdentifier("userKey2"))
                    .willReturn(dummyUser);

            // simulate duplicate email
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
    }

    // You can add similar tests for /secretary and /admin endpoints following this pattern.
}
