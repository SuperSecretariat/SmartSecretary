package com.example.demo.controller;


import com.example.demo.modelDB.Admin;
import com.example.demo.modelDB.Secretary;
import com.example.demo.modelDB.Student;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.SecretaryRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.request.AdminRequest;
import com.example.demo.request.SecretaryRequest;
import com.example.demo.request.StudentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

import static com.example.demo.util.AESUtil.encrypt;

@RestController
@RequestMapping("/api/add")
public class AddController {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    SecretaryRepository secretaryRepository;

    @Autowired
    AdminRepository adminRepository;

    @PostMapping("/student")
    public ResponseEntity<?> addStudent(@RequestBody StudentRequest studentRequest){
        if(studentRepository.existsByRegNumber(studentRequest.getRegistrationNumber()))
        {
            Student newStudent = new Student(studentRequest.getRegistrationNumber(), studentRequest.getEmail());
            studentRepository.save(newStudent);
            return ResponseEntity.ok("New student added to the database");
        }
        else
            return ResponseEntity.status(409).body("Registration number already in use");

    }

    @PostMapping("/secretary")
    public ResponseEntity<?> addSecretary(@RequestBody SecretaryRequest secretaryRequest){
        if(secretaryRepository.existsByAuthKey(secretaryRequest.getAuthKey())){
            Secretary newSecretary = new Secretary(secretaryRequest.getAuthKey(), secretaryRequest.getEmail());
            secretaryRepository.save(newSecretary);
            return ResponseEntity.ok("New secretary added to the database");
        }
        else
            return ResponseEntity.status(409).body("Authentication key already in use");

    }

    @PostMapping("/admin")
    public ResponseEntity<?> addAdmin(@RequestBody AdminRequest adminRequest){
        if(adminRepository.existsByAuthKey(adminRequest.getAuthKey()))
        {
            Admin newAdmin = new Admin(adminRequest.getAuthKey(), adminRequest.getEmail());
            adminRepository.save(newAdmin);
            return ResponseEntity.ok("New admin added to the database");
        }
        else
            return ResponseEntity.status(409).body("Authentication key already in use");


    }
}
