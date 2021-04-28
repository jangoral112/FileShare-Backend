package com.jango.user.controller;

import com.jango.user.dto.CreateUserRequest;
import com.jango.user.dto.UserDetailsWithIdResponse;
import com.jango.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<Map<String, String>> createUser(@RequestBody CreateUserRequest request) {
        String responseMessage = userService.createUser(request);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", responseMessage);
        return ResponseEntity.ok(responseBody);
    }
    
    @GetMapping(value = "/private/details", params = {"email"})
    public ResponseEntity<UserDetailsWithIdResponse> getUserDetailsByEmail(@RequestParam(value = "email") String email) {
        UserDetailsWithIdResponse response = userService.getUserDetailsByEmail(email);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "/private/details", params = {"id"})
    public ResponseEntity<UserDetailsWithIdResponse> getUserDetailsById(@RequestParam(value = "userId") Long userId) {
        UserDetailsWithIdResponse response = userService.getUserDetailsById(userId);
        return ResponseEntity.ok(response);
    }
}
