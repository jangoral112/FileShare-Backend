package com.jango.user.controller;

import com.jango.user.dto.CreateUserRequest;
import com.jango.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        String responseMessage = userService.createUser(request);
        
        return ResponseEntity.ok().header("Content-Type", "text/plain").body(responseMessage);
    }

//    @GetMapping("/protected")
//    public
}
