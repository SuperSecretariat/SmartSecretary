package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class test {
    @GetMapping("/api/ping")
    public String ping() {
        return "pong";
    }
}
