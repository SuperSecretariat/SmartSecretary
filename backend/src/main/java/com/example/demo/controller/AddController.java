package com.example.demo.controller;


import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Secretary;
import com.example.demo.entity.Student;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.SecretaryRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.request.AdminRequest;
import com.example.demo.request.SecretaryRequest;
import com.example.demo.request.StudentRequest;
import com.example.demo.response.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/add")
public class AddController {

    private final StudentRepository studentRepository;
    private final SecretaryRepository secretaryRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public AddController(StudentRepository studentRepository,
                         SecretaryRepository secretaryRepository,
                         AdminRepository adminRepository){
        this.adminRepository = adminRepository;
        this.secretaryRepository = secretaryRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/student")
    public ResponseEntity<JwtResponse> addStudent(@RequestBody StudentRequest studentRequest){
        if(!studentRepository.existsByRegNumber(studentRequest.getRegistrationNumber()))
        {
            Student newStudent = new Student(studentRequest.getRegistrationNumber(), studentRequest.getEmail());
            studentRepository.save(newStudent);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.STUDENT_ADDED));
        }
        else
            return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.REG_NUMBER_IN_USE));

    }

    @PostMapping("/secretary")
    public ResponseEntity<JwtResponse> addSecretary(@RequestBody SecretaryRequest secretaryRequest){
        if(!secretaryRepository.existsByAuthKey(secretaryRequest.getAuthKey())){
            Secretary newSecretary = new Secretary(secretaryRequest.getAuthKey(), secretaryRequest.getEmail());
            secretaryRepository.save(newSecretary);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.SECRETARY_ADDED));
        }
        else
            return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.AUTH_KEY_IN_USE));

    }

    @PostMapping("/admin")
    public ResponseEntity<JwtResponse> addAdmin(@RequestBody AdminRequest adminRequest){
        if(!adminRepository.existsByAuthKey(adminRequest.getAuthKey()))
        {
            Admin newAdmin = new Admin(adminRequest.getAuthKey(), adminRequest.getEmail());
            adminRepository.save(newAdmin);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.ADMIN_ADDED));
        }
        else
            return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.AUTH_KEY_IN_USE));


    }
}
