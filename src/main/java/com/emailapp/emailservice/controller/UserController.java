package com.emailapp.emailservice.controller;

import com.emailapp.emailservice.dto.request.SignupRequest;
import com.emailapp.emailservice.dto.response.ApiResponse;
import com.emailapp.emailservice.dto.response.UserResponse;
import com.emailapp.emailservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")  // Allow React frontend to connect
public class UserController {

    @Autowired
    private UserService userService;

    // POST /api/users/signup - Register new user
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest request) {
        try {
            UserResponse user = userService.registerUser(request);
            return ResponseEntity.ok(
                    new ApiResponse(true, "User registered successfully", user)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // GET /api/users/{userId} - Get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long userId) {
        try {
            UserResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(
                    new ApiResponse(true, "User found", user)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}