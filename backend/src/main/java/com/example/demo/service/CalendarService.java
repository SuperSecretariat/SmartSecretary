package com.example.demo.service;

import com.example.demo.entity.Event;
import com.example.demo.repository.CalendarRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class CalendarService {

    private final CalendarRepository calendarRepository;

    public CalendarService(CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }

    public void processCsv(MultipartFile file) throws IOException {
        List<Event> events = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                Event event = new Event(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]);
                events.add(event);
            }
        }
        calendarRepository.saveAll(events);
    }
}

