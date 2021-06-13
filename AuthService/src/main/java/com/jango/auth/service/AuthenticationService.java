package com.jango.auth.service;

import com.jango.auth.dto.UserAuthenticationResponse;
import com.jango.auth.entity.Role;
import com.jango.auth.entity.User;
import com.jango.auth.exception.IncorrectCredentialsException;
import com.jango.auth.jwt.JsonWebToken;
import com.jango.auth.jwt.JsonWebTokenConfig;
import com.jango.auth.jwt.JsonWebTokenFactory;
import com.jango.auth.repository.UserRepository;
import com.jango.auth.dto.UserAuthenticationRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

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

    public Pair<JsonWebToken, UserAuthenticationResponse> authenticateUser(UserAuthenticationRequest request) {

        User user = userRepository.findUserByEmail(request.getEmail()).orElse(null);

        if((user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) == false) {
            throw new IncorrectCredentialsException("Incorrect email or password");
        }
        List<String> authorities = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        UserAuthenticationResponse response = UserAuthenticationResponse.builder()
                .message("Successfully logged in")
                .authorities(authorities)
                .build();

        return Pair.with(jsonWebTokenFactory.createJwtForUser(user), response);
    }
    
    public Boolean isUserOwnerOfToken(String email, String authHeader) {

        if(authHeader == null || !authHeader.startsWith("Bearer ")) { // TODO throw exception
            return false;
        }

        String jwt = authHeader.replace("Bearer ", "");
        
        try {
            String subject = Jwts.parser()
                                 .setSigningKey(jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8))
                                 .parseClaimsJws(jwt)
                                 .getBody()
                                 .getSubject();
            
            return email.equals(subject);
        } catch (ExpiredJwtException e) { // TODO throw exception
            return false;
        }
    }

    public List<String> parseTokenAuthorities(String authHeader) {

        Claims claims = Jwts.parser()
                .setSigningKey(jwtConfig.getSecretKey().getBytes())
                .parseClaimsJws(authHeader)
                .getBody();

        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");

        return authorities;
    }
}
