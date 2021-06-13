package com.jango.auth.controller;

import com.jango.auth.dto.UserAuthenticationRequest;
import com.jango.auth.dto.UserAuthenticationResponse;
import com.jango.auth.jwt.JsonWebToken;
import com.jango.auth.service.AuthenticationService;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/authUser")
    public ResponseEntity<UserAuthenticationResponse> authenticateUser(@RequestBody UserAuthenticationRequest request) {

        Pair<JsonWebToken, UserAuthenticationResponse> jwtWithResponse = authenticationService.authenticateUser(request);

        return ResponseEntity.ok()
                             .header("Access-Control-Expose-Headers", "Authorization")
                             .header("Authorization", "Bearer " + jwtWithResponse.getValue0().asString())
                             .body(jwtWithResponse.getValue1());
    }
    
    @GetMapping("/validateOwner")
    public ResponseEntity<Boolean> isUserOwnerOfToken(@RequestParam("email") String email,
                                                      @RequestHeader("Authorization") String authHeader) {
        
        Boolean result = authenticationService.isUserOwnerOfToken(email, authHeader);
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/token/authorities")
    public ResponseEntity<List<String>> parseTokenAuthorities(@RequestHeader("Authorization") String authHeader) {

        List<String> response = authenticationService.parseTokenAuthorities(authHeader);

        return ResponseEntity.ok(response);
    }
}
