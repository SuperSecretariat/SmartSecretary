package com.example.demo.config;

import com.example.demo.util.AESUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AESUtilConfig {

    @Bean
    public boolean initializeAESUtil(@Value("${jwt.secret}") String secretKey) {
        AESUtil.initialize(secretKey);
        return true;
    }
}