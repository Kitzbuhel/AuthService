package com.kitzbuhel.idpauthservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitzbuhel.idpauthservice.constants.Constant;
import com.kitzbuhel.idpauthservice.identities.User;
import com.kitzbuhel.idpauthservice.repositories.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) throws JsonProcessingException {
        Map<String, String> response = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();


        // Verify email is valid
        if (!EmailValidator.getInstance().isValid(user.getEmail())) {
            System.out.println("Invalid email");

            response.put("response", "Invalid email");

            return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.BAD_REQUEST);
        }

        // Verify if email already exists
        if (userRepository.findById(user.getEmail()).isPresent()) {
            System.out.println("Email already exists");

            response.put("response", "Email already exists");

            return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.BAD_REQUEST);
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(String.valueOf(user.getPassword().hashCode()));
        newUser.setIsLoggedIn(false);
        newUser.setTimestamp(new Date());
        userRepository.save(newUser);

        System.out.println("User registered successfully");
        response.put("response", "User registered successfully");
        return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.OK);
    }

    @PatchMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) throws JsonProcessingException {
        User existingUser = userRepository.findById(user.getEmail()).orElse(null);
        Map<String, String> response = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        if (existingUser == null) {
            System.out.println("Username or password is incorrect");

            response.put("response", "Username or password is incorrect");
            return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.BAD_REQUEST);
        }

        if (!existingUser.getPassword().equals(String.valueOf(user.getPassword().hashCode()))) {
            System.out.println("Username or password is incorrect");

            response.put("response", "Username or password is incorrect");
            return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.BAD_REQUEST);
        }

        existingUser.setIsLoggedIn(true);
        existingUser.setTimestamp(new Date());
        userRepository.save(existingUser);

        System.out.println("User logged in successfully");
        response.put("response", "User logged in successfully");

        return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.OK);
    }

    @GetMapping("/status/{email}")
    public ResponseEntity<String> getUserStatus(@PathVariable String email) throws JsonProcessingException {
        User existingUser = userRepository.findById(email).orElse(null);
        Map<String, String> response = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        if (existingUser == null) {
            System.out.println("User is not registered");

            response.put("response", "User is not registered");
            response.put("value", "false");
            return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.BAD_REQUEST);
        }

        if (!existingUser.getIsLoggedIn()) {
            System.out.println("User is not logged in");

            response.put("response", "User is not logged in");
            response.put("value", "false");
            return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.OK);
        }

        // Compare if the difference between the current time and the last login time is greater than 1 hour
        if ((new Date().getTime() - existingUser.getTimestamp().getTime()) > Constant.HOUR) {
            System.out.println("User session has expired");

            existingUser.setIsLoggedIn(false);
            userRepository.save(existingUser);

            response.put("response", "User session has expired");
            response.put("value", "false");
            return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.BAD_REQUEST);
        }

        System.out.println("User is logged in");
        response.put("response", "User is logged in");
        response.put("value", "true");

        return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.OK);
    }
}
