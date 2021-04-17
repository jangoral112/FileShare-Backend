package com.jango.auth.controller;

import com.jango.auth.dto.UserAuthenticationRequest;
import com.jango.auth.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/authUser")
    public ResponseEntity<String> authenticateUser(@RequestBody UserAuthenticationRequest request) {

        String authToken = authenticationService.authenticateUser(request);

        return ResponseEntity.ok().header("Authorization", "bearer" + authToken).build();
    }


}
