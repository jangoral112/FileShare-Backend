package com.jango.auth.service;

import com.jango.auth.entity.User;
import com.jango.auth.exception.IncorrectCredentialsException;
import com.jango.auth.jwt.JsonWebTokenConfig;
import com.jango.auth.jwt.JsonWebTokenFactory;
import com.jango.auth.repository.UserRepository;
import com.jango.auth.dto.UserAuthenticationRequest;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JsonWebTokenFactory jsonWebTokenFactory;
    
    @Autowired
    private JsonWebTokenConfig jwtConfig;

    public String authenticateUser(UserAuthenticationRequest request) {

        User user = userRepository.findUserByEmail(request.getEmail()).orElse(null);

        if((user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) == false) {
            throw new IncorrectCredentialsException("Incorrect email or password");
        }

        return jsonWebTokenFactory.createJwtForUser(user).asString();
    }
    
    public Boolean isUserOwnerOfToken(String email, String token) {
        
        String subject = Jwts.parser()
                             .setSigningKey(jwtConfig.getSecretKey())
                             .parseClaimsJws(token)
                             .getBody()
                             .getSubject();

        return email.equals(subject);
    }
}
