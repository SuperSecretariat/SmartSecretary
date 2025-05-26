package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.entity.Event;
import com.example.demo.model.enums.ERole;
import com.example.demo.repository.EventRepository;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final EventRepository eventRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public CalendarController(EventRepository eventRepository,
                              JwtUtil jwtUtil,
                              UserDetailsServiceImpl userDetailsService) {
        this.eventRepository = eventRepository;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/add")
    public ResponseEntity<JwtResponse> uploadCalendar(@RequestHeader("Authorization") String headerAuth,
                                                      @RequestParam("file") MultipartFile file) {
        try {
//            String token = headerAuth.substring(7);
//            if (!jwtUtil.validateJwtToken(token)) {
//                return ResponseEntity.status(400).body(new JwtResponse(ErrorMessage.INVALID_DATA));
//            }
//
//            String regNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
//            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(regNumber);
//
//            if (!userDetails.hasRole(ERole.ROLE_SECRETARY)) {
//                return ResponseEntity.status(403).body(new JwtResponse(ErrorMessage.ACCESS_FORBIDDEN));
//            }

            if (file.isEmpty()) {
                return ResponseEntity.status(400).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    String[] parts = line.split(",");
                    if (parts.length < 6) {
                        return ResponseEntity.status(400).body(new JwtResponse(ErrorMessage.WRONG_FORMAT));
                    }

                    Event event = new Event(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim(),
                            parts[5].trim()
                    );

                    eventRepository.save(event);
                }
            }
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.CALENDAR_UPLOAD));

        } catch (IOException e) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ERROR_FILE));
        }


    }

    @PostMapping("/fetch-group")
    public ResponseEntity<JwtResponse> getCalendarByGroup(@RequestHeader("Authorization") String headerAuth,
                                                          @RequestParam String group){
//        String token = headerAuth.substring(7);
//        if (!jwtUtil.validateJwtToken(token)) {
//            return ResponseEntity.status(400).body(new JwtResponse(ErrorMessage.INVALID_DATA));
//        }
//
//        String regNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
//        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(regNumber);
//
//        if (!userDetails.hasRole(ERole.ROLE_STUDENT)) {
//            return ResponseEntity.status(403).body(new JwtResponse(ErrorMessage.ACCESS_FORBIDDEN));
//        }

        List<Event> eventList = eventRepository.findAllByGroup(group);
        if(!eventList.isEmpty()) {
            return ResponseEntity.ok(new JwtResponse(eventList));
        } else {
            System.out.println("No events found for group: " + group);
            return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NO_DATA_GROUP));
        }


    }
}
