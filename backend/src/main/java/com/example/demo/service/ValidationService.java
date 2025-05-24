package com.example.demo.service;

import com.example.demo.entity.Admin;
import com.example.demo.entity.Secretary;
import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import com.example.demo.exceptions.DecryptionException;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.SecretaryRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.demo.util.AESUtil.decrypt;

@Service
public class ValidationService {
    private final StudentRepository studentRepository;
    private final SecretaryRepository secretaryRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private static final Logger loggerValidationService = LoggerFactory.getLogger(ValidationService.class);

    @Autowired
    public ValidationService(
            StudentRepository studentRepository,
            SecretaryRepository secretaryRepository,
            AdminRepository adminRepository,
            UserRepository userRepository
    ){
       this.adminRepository = adminRepository;
       this.secretaryRepository = secretaryRepository;
       this.studentRepository = studentRepository;
       this.userRepository = userRepository;
    }

    public boolean isAuthKeyUsed(String decryptAuthKey){
        for(Secretary secretary : secretaryRepository.findAll()){
            try{
                String tempAuthKey = decrypt(secretary.getAuthKey());
                if(tempAuthKey.equals(decryptAuthKey)){
                    return true;
                }
            } catch (DecryptionException e) {
                loggerValidationService.error(e.getMessage());
                return true;
            }
        }

        for(Admin admin : adminRepository.findAll()){
            try{
                String tempAuthKey = decrypt(admin.getAuthKey());
                if(tempAuthKey.equals(decryptAuthKey)){
                    return true;
                }
            } catch (DecryptionException e) {
                loggerValidationService.error(e.getMessage());
                return true;
            }
        }

        for(Student student : studentRepository.findAll()){
            if(student.getRegNumber().equals(decryptAuthKey))
            {
                return true;
            }

        }

        return false;

    }

    public boolean isEmailUsed(String email){
        for(Student student : studentRepository.findAll()){
            if(student.getEmail().equals(email))
                return true;
        }

        for(Admin admin : adminRepository.findAll()){
            if(admin.getEmail().equals(email))
                return true;
        }

        for(Secretary secretary : secretaryRepository.findAll()){
            if(secretary.getEmail().equals(email))
                return true;
        }

        return false;
    }

    public User findUserByIdentifier(String identifier){
        boolean emailExists = userRepository.existsByEmail(identifier);
        if(emailExists){
            Optional<User> userOptional = userRepository.findByEmail(identifier);
            if(userOptional.isPresent()){
                return userOptional.get();
            }
        }
        try{
            for(User user : userRepository.findAll()){
                if(findStudent(user.getRegNumber()) != null){
                    if(user.getRegNumber().equals(identifier)){
                        return user;
                    }
                }
                else{
                    String decrypted = decrypt(user.getRegNumber());
                    if(decrypted.equals(identifier)){
                        return user;
                    }

                }
            }
        }catch(DecryptionException ex){
            loggerValidationService.error(ex.getMessage());
            return null;
        }
        return null;
    }

    public Admin findAdmin(String decryptAuthKey) throws DecryptionException{
        for(Admin admin : adminRepository.findAll()){
            try{
                String tempAuthKey = decrypt(admin.getAuthKey());
                if(tempAuthKey.equals(decryptAuthKey)){
                    return admin;
                }
            } catch (DecryptionException e) {
                loggerValidationService.error(e.getMessage());
            }
        }
        return null;
    }

    public Secretary findSecretary(String decryptAuthKey) throws DecryptionException{
        for(Secretary secretary : secretaryRepository.findAll()){
            try{
                String tempAuthKey = decrypt(secretary.getAuthKey());
                if(tempAuthKey.equals(decryptAuthKey)){
                    return secretary;
                }
            } catch (DecryptionException e) {
                loggerValidationService.error(e.getMessage());
            }
        }
        return null;
    }

    public Student findStudent(String decryptAuthKey){
        for(Student student : studentRepository.findAll()){
            if(student.getRegNumber().equals(decryptAuthKey))
                return student;
        }
        return null;
    }
}
