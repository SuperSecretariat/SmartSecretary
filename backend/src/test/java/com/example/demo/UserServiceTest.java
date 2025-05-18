package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenValidUserId_thenUserShouldBeFound() {
        User user = new User();
        user.setRegNumber("1111");
        when(userRepository.findByRegNumber("1111"))
                .thenReturn(Optional.of(user));
        UserDetails result = userService.loadUserByUsername("1111");

        assertNotNull(result);
        assertTrue(result instanceof UserDetailsImpl);
        UserDetailsImpl found = (UserDetailsImpl) result;
        assertEquals("1111", found.getUsername());
        verify(userRepository, times(1)).findByRegNumber("1111");

    }
}
