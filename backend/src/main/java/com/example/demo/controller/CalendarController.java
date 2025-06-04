package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.dto.calendar.UploadResponse;
import com.example.demo.entity.calendar.CalendarFile;
import com.example.demo.entity.calendar.Event;
import com.example.demo.entity.calendar.Exam;
import com.example.demo.entity.calendar.Year;
import com.example.demo.repository.calendar.EventRepository;
import com.example.demo.repository.calendar.CalendarFileRepository;
import com.example.demo.repository.calendar.ExamRepository;
import com.example.demo.repository.calendar.YearlyRepository;
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
    private final ExamRepository examRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final CalendarFileRepository calendarFileRepository;
    private final YearlyRepository yearlyRepository;

    @Autowired
    public CalendarController(EventRepository eventRepository,
                              ExamRepository examRepository,
                              JwtUtil jwtUtil,
                              UserDetailsServiceImpl userDetailsService,
                              CalendarFileRepository calendarFileRepository, YearlyRepository yearlyRepository) {
        this.eventRepository = eventRepository;
        this.examRepository = examRepository;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.calendarFileRepository = calendarFileRepository;
        this.yearlyRepository = yearlyRepository;
    }

      // ---------------------------------------------------------------------------------------------------- //
     // -------------------------------------- Event Calendar Functions ------------------------------------ //
    // ---------------------------------------------------------------------------------------------------- //

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
                    groupsJoined,
                    "Event"
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
            return ResponseEntity.ok(JwtResponse.fromEvents(eventList));
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
        List<CalendarFile> files = calendarFileRepository.findAllByCategory("Event");
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<JwtResponse> deleteAllCalendarFiles() {
        try {
            eventRepository.deleteAll();
            calendarFileRepository.deleteByCategory("Event");
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
            calendarFileRepository.deleteByGroupAndCategory(group,"Event");
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.CALENDAR_DELETE));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ERROR_FILE));
        }
    }

      // ---------------------------------------------------------------------------------------------------- //
     // -------------------------------------- Exam Calendar Functions ------------------------------------- //
    // ---------------------------------------------------------------------------------------------------- //

    @PostMapping("/exam/fetch-group")
    public ResponseEntity<JwtResponse> getExamCalendarByGroup(@RequestHeader("Authorization") String headerAuth,
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

        List<Exam> examList = examRepository.findAllByGroup(group);
        if(!examList.isEmpty()) {
            return ResponseEntity.ok(JwtResponse.fromExams(examList));
        } else {
            System.out.println("No exams found for group: " + group);
            return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NO_DATA_GROUP));
        }
    }

    @PostMapping("/add-exam")
    public ResponseEntity<UploadResponse> uploadExam(@RequestHeader("Authorization") String headerAuth,
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
                        if( headers.length < 5) {
                            return ResponseEntity
                                    .status(400)
                                    .body(new UploadResponse(ErrorMessage.WRONG_EXAM_HEADER));
                        }
                        if(!headers[0].trim().equals("Group") ||
                                !headers[1].trim().equals("Date") ||
                                !headers[2].trim().equals("Type") ||
                                !headers[3].trim().equals("Time") ||
                                !headers[4].trim().equals("Title")) {
//                            System.out.println(headers[0].trim());
//                            System.out.println(headers[1].trim());
//                            System.out.println(headers[2].trim());
//                            System.out.println(headers[3].trim());
//                            System.out.println(headers[4].trim());

                            return ResponseEntity
                                    .status(400)
                                    .body(new UploadResponse(ErrorMessage.WRONG_EXAM_HEADER));
                        }
                        isFirstLine = false;
                        continue;
                    }

                    String[] parts = line.split(",");
                    if (parts.length < 5) {
                        return ResponseEntity
                                .status(400)
                                .body(new UploadResponse(ErrorMessage.WRONG_FORMAT));
                    }

                    groups.add(parts[0].trim());

                    Exam exam = new Exam(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim()
                    );

                    //if we have more than one group
                    if(groups.size()> 1) {
                        return ResponseEntity
                                .status(400)
                                .body(new UploadResponse(ErrorMessage.MULTIPLE_GROUPS));
                    }

                    examRepository.save(exam);
                }
            }
            float fileSizeKB = (float) file.getSize() / 1024;
            String groupsJoined = String.join(", ", groups);

            CalendarFile calendarFile = new CalendarFile(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    fileSizeKB,
                    groupsJoined,
                    "Exam"
            );

            calendarFileRepository.save(calendarFile);
            return ResponseEntity.ok(new UploadResponse(ValidationMessage.CALENDAR_UPLOAD, groups));

        } catch (IOException e) {
            return ResponseEntity
                    .status(500)
                    .body(new UploadResponse(ErrorMessage.ERROR_FILE));
        }
    }

    @GetMapping("/exam/files")
    public ResponseEntity<List<CalendarFile>> getAllExamCalendarFiles() {
        if(calendarFileRepository.count() == 0) {
            return ResponseEntity.status(404).body(Collections.emptyList());
        }
        List<CalendarFile> files = calendarFileRepository.findAllByCategory("Exam");
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/exam/delete-group/{group}")
    public ResponseEntity<JwtResponse> deleteExamCalendarByGroup(@PathVariable String group) {
        // Validate group name
        if (group == null || group.isEmpty()) {
            return ResponseEntity.status(400).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        }

        // Check if there are exams for the group
        try {
            List<Exam> exams = examRepository.findAllByGroup(group);
            if (exams.isEmpty()) {
                return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NO_DATA_GROUP));
            }
            examRepository.deleteByGroup(group);
            calendarFileRepository.deleteByGroupAndCategory(group,"Exam");
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.CALENDAR_DELETE));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ERROR_FILE));
        }
    }

    @DeleteMapping("/exam/delete-all")
    public ResponseEntity<JwtResponse> deleteAllExamCalendarFiles() {
        try {
            examRepository.deleteAll();
            calendarFileRepository.deleteByCategory("Exam");
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.CALENDAR_DELETE));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ERROR_FILE));
        }
    }

      // ---------------------------------------------------------------------------------------------------- //
     // ------------------------------------- Yearly Calendar Functions ------------------------------------ //
    // ---------------------------------------------------------------------------------------------------- //

    @PostMapping("/yearly/fetch")
    public ResponseEntity<JwtResponse> getYearlyCalendar(@RequestHeader("Authorization") String headerAuth) {
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

        List<Year> yearList = yearlyRepository.findAll();
        if(!yearList.isEmpty()) {
            return ResponseEntity.ok(JwtResponse.fromYears(yearList));
        } else {
            System.out.println("No yearly calendar found for now...");
            return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NO_DATA_GROUP));
        }
    }

    @PostMapping("/yearly/add")
    public ResponseEntity<JwtResponse> uploadYearlyCalendar(@RequestHeader("Authorization") String headerAuth,
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
                        .status(401)
                        .body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        String[] headers = line.split(",");
                        if( headers.length < 3) {
                            return ResponseEntity
                                    .status(402)
                                    .body(new JwtResponse(ErrorMessage.WRONG_EXAM_HEADER));
                        }
                        if(!headers[0].trim().equals("Start") ||
                                !headers[1].trim().equals("End") ||
                                !headers[2].trim().equals("Type")) {
//                            System.out.println(headers[0].trim());
//                            System.out.println(headers[1].trim());
//                            System.out.println(headers[2].trim());
//                            System.out.println(headers[3].trim());
//                            System.out.println(headers[4].trim());

                            return ResponseEntity
                                    .status(403)
                                    .body(new JwtResponse(ErrorMessage.WRONG_YEARLY_HEADER));
                        }
                        isFirstLine = false;
                        continue;
                    }

                    String[] parts = line.split(",");
                    if (parts.length < 3) {
                        return ResponseEntity
                                .status(400)
                                .body(new JwtResponse(ErrorMessage.WRONG_FORMAT));
                    }

                    Year year = new Year(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim()
                    );

                    yearlyRepository.save(year);
                }
            }
            float fileSizeKB = (float) file.getSize() / 1024;

            CalendarFile calendarFile = new CalendarFile(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    fileSizeKB,
                    "NoGroup",
                    "Year"
            );

            calendarFileRepository.save(calendarFile);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.CALENDAR_UPLOAD));

        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(new JwtResponse(ErrorMessage.ERROR_FILE));
        }
    }

    @GetMapping("/yearly/files")
    public ResponseEntity<List<CalendarFile>> getAllYearCalendarFiles() {
        if(calendarFileRepository.count() == 0) {
            return ResponseEntity.status(404).body(Collections.emptyList());
        }
        List<CalendarFile> files = calendarFileRepository.findAllByCategory("Year");
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/yearly/delete-all")
    public ResponseEntity<JwtResponse> deleteAllYearCalendarFiles() {
        try {
            yearlyRepository.deleteAll();
            calendarFileRepository.deleteByCategory("Year");
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.CALENDAR_DELETE));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ERROR_FILE));
        }
    }

}
