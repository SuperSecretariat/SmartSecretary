package com.example.demo.controller;

import com.example.demo.controller.Student;
import org.springframework.stereotype.Service;
import com.example.demo.controller.StudentRepository;

import java.util.List;

@Service
public class StudentService {
    private StudentRepository studentRepository;
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    public List<Student> findAll() {
        return studentRepository.findAll();
    }
    public Student findById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }
    public void save(Student document) {
        studentRepository.save(document);
    }
    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }
}