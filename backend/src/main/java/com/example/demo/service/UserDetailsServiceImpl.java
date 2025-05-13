package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String regNumber){
        Optional<User> verifyUser = userRepository.findByRegNumber(regNumber);
        if(verifyUser.isPresent()){
            return UserDetailsImpl.build(verifyUser.get());
        }
        else {
            throw new UsernameNotFoundException("User not found!");
        }
    }

}
