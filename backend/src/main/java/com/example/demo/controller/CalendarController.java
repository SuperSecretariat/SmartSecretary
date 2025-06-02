package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.dto.calendar.UploadResponse;
import com.example.demo.entity.CalendarFile;
import com.example.demo.entity.Event;
import com.example.demo.repository.calendar.EventRepository;
import com.example.demo.repository.calendar.CalendarFileRepository;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final EventRepository eventRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final CalendarFileRepository calendarFileRepository;

    @Autowired
    public CalendarController(EventRepository eventRepository,
                              JwtUtil jwtUtil,
                              UserDetailsServiceImpl userDetailsService,
                              CalendarFileRepository calendarFileRepository) {
        this.eventRepository = eventRepository;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.calendarFileRepository = calendarFileRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<UploadResponse> uploadCalendar(@RequestHeader("Authorization") String headerAuth,
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
                return ResponseEntity
                        .status(400)
                        .body(new UploadResponse(ErrorMessage.INVALID_DATA));
            }

            Set<String> groups = new HashSet<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        String[] headers = line.split(",");
                        if( headers.length < 6) {
                            return ResponseEntity
                                    .status(400)
                                    .body(new UploadResponse(ErrorMessage.WRONG_HEADER));
                        }
                        if(!headers[0].trim().equals("Group") ||
                                !headers[1].trim().equals("Type") ||
                                !headers[2].trim().equals("Day") ||
                                !headers[3].trim().equals("Time") ||
                                !headers[4].trim().equals("Title") ||
                                !headers[5].trim().equals("Professor")) {
                            return ResponseEntity
                                    .status(400)
                                    .body(new UploadResponse(ErrorMessage.WRONG_HEADER));
                        }
                        isFirstLine = false;
                        continue;
                    }

                    String[] parts = line.split(",");
                    if (parts.length < 6) {
                        return ResponseEntity
                                .status(400)
                                .body(new UploadResponse(ErrorMessage.WRONG_FORMAT));
                    }

                    groups.add(parts[0].trim());

                    Event event = new Event(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim(),
                            parts[5].trim()
                    );

                    //if we have more than one group
                    if(groups.size()> 1) {
                        return ResponseEntity
                                .status(400)
                                .body(new UploadResponse(ErrorMessage.MULTIPLE_GROUPS));
                    }

                    eventRepository.save(event);
                }
            }
            float fileSizeKB = (float) file.getSize() / 1024;
            String groupsJoined = String.join(", ", groups);

            CalendarFile calendarFile = new CalendarFile(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    fileSizeKB,
                    groupsJoined
            );

            calendarFileRepository.save(calendarFile);
            return ResponseEntity.ok(new UploadResponse(ValidationMessage.CALENDAR_UPLOAD, groups));

        } catch (IOException e) {
            return ResponseEntity
                    .status(500)
                    .body(new UploadResponse(ErrorMessage.ERROR_FILE));
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

    @GetMapping("/files")
    public ResponseEntity<List<CalendarFile>> getAllCalendarFiles() {
        if(calendarFileRepository.count() == 0) {
            return ResponseEntity.status(404).body(Collections.emptyList());
        }
        List<CalendarFile> files = calendarFileRepository.findAll();
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<JwtResponse> deleteAllCalendarFiles() {
        try {
            eventRepository.deleteAll();
            calendarFileRepository.deleteAll();
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.CALENDAR_DELETE));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ERROR_FILE));
        }
    }

    @DeleteMapping("/delete-group/{group}")
    public ResponseEntity<JwtResponse> deleteCalendarByGroup(@PathVariable String group) {
        // Validate group name
        if (group == null || group.isEmpty()) {
            return ResponseEntity.status(400).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        }

        // Check if there are events for the group
      try {
            List<Event> events = eventRepository.findAllByGroup(group);
            if (events.isEmpty()) {
                return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NO_DATA_GROUP));
            }
            eventRepository.deleteByGroup(group);
            calendarFileRepository.deleteByGroups(group);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.CALENDAR_DELETE));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ERROR_FILE));
        }
    }

}
