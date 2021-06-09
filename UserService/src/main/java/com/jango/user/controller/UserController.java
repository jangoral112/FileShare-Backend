package com.jango.user.controller;

import com.jango.user.dto.CreateUserRequest;
import com.jango.user.dto.UserDetailsResponse;
import com.jango.user.dto.UserDetailsWithIdResponse;
import com.jango.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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

    @GetMapping(path = "{email}")
    public ResponseEntity<UserDetailsResponse> getUserDetails(@PathVariable("email") String email) {
        UserDetailsResponse userDetailsResponse = userService.getUserDetailsByEmail(email);
        return ResponseEntity.ok(userDetailsResponse);
    }

    @GetMapping
    public ResponseEntity<List<UserDetailsResponse>> getUsersDetailsSortedByPhraseFit(
                                                                        @RequestParam(name = "phrase") String phrase) {
        List<UserDetailsResponse> response = userService.getUsersDetailsSortedByPhraseFit(phrase);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/private/details", params = {"email"})
    public ResponseEntity<UserDetailsWithIdResponse> getUserDetailsByEmail(@RequestParam(value = "email") String email) {
        UserDetailsWithIdResponse response = userService.getUserDetailsWithIdByEmail(email);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "/private/details", params = {"id"})
    public ResponseEntity<UserDetailsWithIdResponse> getUserDetailsById(@RequestParam(value = "userId") Long userId) {
        UserDetailsWithIdResponse response = userService.getUserDetailsWithIdById(userId);
        return ResponseEntity.ok(response);
    }
}
