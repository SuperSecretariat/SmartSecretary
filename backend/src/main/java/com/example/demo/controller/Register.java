    package com.example.demo.controller;

    import jakarta.validation.Valid;
    import jakarta.validation.constraints.NotBlank;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import java.util.Optional;


    @RestController
    @RequestMapping("api/auth")
    public class Register {

        @Autowired
        private UserRepository userRepository;

        @PostMapping("/register")
        public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest)
        {
            Optional<User> optionalUser = userRepository.findByIdNumber(registerRequest.getIdNumber());
            if(optionalUser.isPresent())
            {
                return ResponseEntity.status(409).body("Un cont cu acelasi numar matricol a fost creat deja");
            }
            else
            {
                User newUser = new User(registerRequest.getLastName(), registerRequest.getFirstName(),registerRequest.getIdNumber(), registerRequest.getEmail(), registerRequest.getPassword());
                userRepository.save(newUser);

                return ResponseEntity.ok("Cont create cu succes!");
            }

        }


    }
