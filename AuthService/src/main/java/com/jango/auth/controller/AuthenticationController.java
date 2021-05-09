package com.jango.auth.controller;

import com.jango.auth.dto.UserAuthenticationRequest;
import com.jango.auth.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/authUser")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody UserAuthenticationRequest request) {

        String authToken = authenticationService.authenticateUser(request);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Successfully logged in");

        return ResponseEntity.ok()
                             .header("Access-Control-Expose-Headers", "Authorization")
                             .header("Authorization", "Bearer " + authToken)
                             .body(responseBody);
    }
    
    @GetMapping("/validateOwner")
    public ResponseEntity<Boolean> isUserOwnerOfToken(@RequestParam("email") String email, @RequestParam("token") String token) {
        
        Boolean result = authenticationService.isUserOwnerOfToken(email, token);
        
        return ResponseEntity.ok(result);
    }


}
