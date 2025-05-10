package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Only deletes from the `users` table.
 */

@RestController
@RequestMapping("/api/delete")
public class DeleteController {

    private final UserRepository userRepo;

    @Autowired
    public DeleteController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    @DeleteMapping("/{regNumber}")
    public ResponseEntity<JwtResponse> deleteByRegNumber(@PathVariable String regNumber) {
        if (Boolean.FALSE.equals(userRepo.existsByRegNumber(regNumber))) {
            return ResponseEntity.status(404)
                    .body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
        }

        userRepo.deleteByRegNumber(regNumber);
        return ResponseEntity.ok(new JwtResponse(ValidationMessage.ACCOUNT_DELETED));
    }


    @DeleteMapping("/me")
    public ResponseEntity<JwtResponse> deleteCurrentUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        if (!userRepo.existsById(userId)) {
            return ResponseEntity.status(404)
                    .body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
        }

        userRepo.deleteById(userId);
        return ResponseEntity.ok(new JwtResponse(ValidationMessage.ACCOUNT_DELETED));
    }
}
