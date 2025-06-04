package com.example.demo;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.controller.AuthController;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Role;
import com.example.demo.entity.Secretary;
import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import com.example.demo.model.enums.ERole;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.TokenBlacklistService;
import com.example.demo.service.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.demo.service.ValidationService;
import com.example.demo.util.AESUtil;
import com.example.demo.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock private ValidationService validationService;
    @Mock private EmailService emailService;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TokenBlacklistService tokenBlacklistService;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private com.example.demo.service.UserDetailsServiceImpl userDetailsService;

    @InjectMocks private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    // --- login tests ---

    @Test
    void login_blankFields_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"registrationNumber\":\"\",\"password\":\"\"}"))
                .andExpect(status().isNotFound());
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
        User user = new User();
        user.setRegNumber("U1");
        user.setPassword("enc1");
        when(validationService.findUserByIdentifier("U1")).thenReturn(user);
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        when(userDetailsService.loadUserByUsername("U1")).thenReturn(UserDetailsImpl.build(user));
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"registrationNumber\":\"U1\",\"password\":\"bad\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.responseMessage").value(ErrorMessage.INCORRECT_PASSWORD));
    }

    @Test
    void login_success_returns200() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setRegNumber("U2");
        user.setPassword("enc2");
        user.setRoles(Set.of(new Role(ERole.ROLE_STUDENT)));
        when(validationService.findUserByIdentifier("U2")).thenReturn(user);
        when(passwordEncoder.matches("pw","enc2")).thenReturn(true);
        when(userDetailsService.loadUserByUsername("U2")).thenReturn(UserDetailsImpl.build(user));
        when(jwtUtil.generateJwtToken(any())).thenReturn("tok2");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"registrationNumber\":\"U2\",\"password\":\"pw\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("tok2"));
    }

    // --- register tests ---

    @Test
    void register_missingFields_returns404() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void register_passwordMismatch_returns404() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"A\",\"lastName\":\"B\",\"registrationNumber\":\"R\",\"email\":\"e@e.com\",\"password\":\"p1\",\"confirmationPassword\":\"p2\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void register_emailInUse_returns409() throws Exception {
        when(userRepository.existsByEmail("e@e.com")).thenReturn(true);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"A\",\"lastName\":\"B\",\"registrationNumber\":\"R\",\"email\":\"e@e.com\",\"password\":\"p\",\"confirmationPassword\":\"p\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void register_studentSuccess_returns200() throws Exception {
        when(userRepository.existsByEmail("stu@e.com")).thenReturn(false);
        when(validationService.findStudent("R1")).thenReturn(new Student("R1","stu@e.com"));
        when(validationService.findSecretary("R1")).thenReturn(null);
        when(validationService.findAdmin("R1")).thenReturn(null);
        when(roleRepository.findByName(ERole.ROLE_STUDENT)).thenReturn(Optional.of(new Role(ERole.ROLE_STUDENT)));
        when(passwordEncoder.encode("p")).thenReturn("encp");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"S\",\"lastName\":\"T\",\"registrationNumber\":\"R1\",\"email\":\"stu@e.com\",\"password\":\"p\",\"confirmationPassword\":\"p\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseMessage").value(ValidationMessage.ACCOUNT_SUCCESS));
    }

    @Test
    void register_secretarySuccess_returns200() throws Exception {
        when(userRepository.existsByEmail("sec@e.com")).thenReturn(false);
        when(validationService.findStudent("R2")).thenReturn(null);
        when(validationService.findSecretary("R2")).thenReturn(new Secretary("R2","sec@e.com"));
        when(validationService.findAdmin("R2")).thenReturn(null);
        when(roleRepository.findByName(ERole.ROLE_SECRETARY)).thenReturn(Optional.of(new Role(ERole.ROLE_SECRETARY)));
        when(passwordEncoder.encode("p")).thenReturn("encp");
        try (MockedStatic<AESUtil> aes = mockStatic(AESUtil.class)) {
            aes.when(() -> AESUtil.decrypt("R2")).thenReturn("R2");
            aes.when(() -> AESUtil.encrypt("R2")).thenReturn("encR2");
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firstName\":\"S\",\"lastName\":\"E\",\"registrationNumber\":\"R2\",\"email\":\"sec@e.com\",\"password\":\"p\",\"confirmationPassword\":\"p\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.responseMessage").value(ValidationMessage.ACCOUNT_SUCCESS));
        }
    }

    @Test
    void register_adminSuccess_returns200() throws Exception {
        when(userRepository.existsByEmail("adm@e.com")).thenReturn(false);
        when(validationService.findStudent("R3")).thenReturn(null);
        when(validationService.findSecretary("R3")).thenReturn(null);
        when(validationService.findAdmin("R3")).thenReturn(new Admin("R3","adm@e.com"));
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(new Role(ERole.ROLE_ADMIN)));
        when(passwordEncoder.encode("p")).thenReturn("encp");
        try (MockedStatic<AESUtil> aes = mockStatic(AESUtil.class)) {
            aes.when(() -> AESUtil.decrypt("R3")).thenReturn("R3");
            aes.when(() -> AESUtil.encrypt("R3")).thenReturn("encR3");
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firstName\":\"A\",\"lastName\":\"D\",\"registrationNumber\":\"R3\",\"email\":\"adm@e.com\",\"password\":\"p\",\"confirmationPassword\":\"p\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.responseMessage").value(ValidationMessage.ACCOUNT_SUCCESS));
        }
    }

    // --- logout tests ---

    @Test
    void logout_withValidToken_returns200() throws Exception {
        String token = "tokenX";
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseMessage").value(ValidationMessage.LOGOUT_SUCCESS));
    }

    @Test
    void logout_noBearer_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.responseMessage").value(ErrorMessage.UNKNOWN_ERROR));
    }

    // --- forgot-password tests ---

    @Test
    void forgotPassword_userNotFound_returns404() throws Exception {
        when(userRepository.findByEmail("no@e.com")).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"no@e.com\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.responseMessage").value(ErrorMessage.NON_EXISTENT_USER));
    }

    @Test
    void forgotPassword_success_returns200() throws Exception {
        User u = new User(); u.setEmail("u@e.com");
        when(userRepository.findByEmail("u@e.com")).thenReturn(Optional.of(u));
        when(jwtUtil.generatePasswordResetToken("u@e.com")).thenReturn("rtok");
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"u@e.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseMessage").value(ValidationMessage.EMAIL_SUCCESS));
    }

    // --- reset-password tests ---

    @Test
    void resetPassword_invalidToken_returns401() throws Exception {
        when(jwtUtil.validateJwtToken("bad")).thenReturn(false);
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"bad\",\"newPassword\":\"np\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.responseMessage").value(ErrorMessage.INVALID_DATA));
    }

    @Test
    void resetPassword_userNotFound_returns404() throws Exception {
        when(jwtUtil.validateJwtToken("rtok")).thenReturn(true);
        when(jwtUtil.getRegistrationNumberFromJwtToken("rtok")).thenReturn("no@e.com");
        when(userRepository.findByEmail("no@e.com")).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"rtok\",\"newPassword\":\"np\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.responseMessage").value(ErrorMessage.NON_EXISTENT_USER));
    }

    @Test
    void resetPassword_success_returns200() throws Exception {
        when(jwtUtil.validateJwtToken("rtok")).thenReturn(true);
        when(jwtUtil.getRegistrationNumberFromJwtToken("rtok")).thenReturn("u@e.com");
        User u = new User(); u.setEmail("u@e.com");
        when(userRepository.findByEmail("u@e.com")).thenReturn(Optional.of(u));
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"rtok\",\"newPassword\":\"np\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseMessage").value(ValidationMessage.UPDATE_SUCCESS));
    }

    // --- me tests ---

    @Test
    void getCurrentUser_returns200() throws Exception {
        User u = new User(); u.setId(99L); u.setRegNumber("RG99");
        var userDetails = UserDetailsImpl.build(u);
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .defaultRequest(get("/api/auth/me").principal(
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                ))
                .build();
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.registrationNumber").value("RG99"));
    }
}
