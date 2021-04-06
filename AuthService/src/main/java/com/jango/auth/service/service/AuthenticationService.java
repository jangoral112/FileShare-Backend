package com.jango.auth.service.service;

import com.jango.auth.service.dto.UserAuthenticationRequest;
import com.jango.auth.service.entity.User;
import com.jango.auth.service.exception.IncorrectCredentialsException;
import com.jango.auth.service.jwt.JsonWebTokenFactory;
import com.jango.auth.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JsonWebTokenFactory jsonWebTokenFactory;

    public String authenticateUser(UserAuthenticationRequest request) {

        User user = userRepository.findUserByEmail(request.getEmail()).orElse(null);

        if((user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) == false) {
            throw new IncorrectCredentialsException("Incorrect email or password");
        }

        return jsonWebTokenFactory.createJwtForUser(user).asString();
    }
}
