package com.emailapp.emailservice.controller;

import com.emailapp.emailservice.dto.request.LoginRequest;
import com.emailapp.emailservice.dto.response.ApiResponse;
import com.emailapp.emailservice.dto.response.AuthResponse;
import com.emailapp.emailservice.entity.User;
import com.emailapp.emailservice.repository.UserRepository;
import com.emailapp.emailservice.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private UserRepository UserRepository;
    @Autowired
    private UserRepository userRepository;

    // POST /api/auth/login - User login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        try{
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Get user details
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate JWT token from user details
            String token = jwtTokenUtil.generateToken(user.getUsername(), user.getUserId());

            AuthResponse authResponse = new AuthResponse(
                    token,
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail()
            );

            return ResponseEntity.ok(
                    new ApiResponse(true, "Login successful", authResponse)
            );
        }catch(BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid username or password", null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Login Failed: "+e.getMessage(), null));
        }
    }

    // GET /api/auth/validate - Validate token
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse> validate(@RequestHeader("Authorixation") String authHeader) {

        try{
            if(authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if(jwtTokenUtil.validateToken(token)) {
                    String username = jwtTokenUtil.extractUsername(token);
                    Long userId = jwtTokenUtil.extractUserId(token);
                    return ResponseEntity.ok(new ApiResponse(
                            true,
                            "Token is valid",
                            new AuthResponse(token, userId, username, null)
                            ));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(
                    false,
                    "Invalid token",
                    null
            ));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(
                    false,
                    "Token validation failed",
                    null
            ));
        }
    }
}
