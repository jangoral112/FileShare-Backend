package com.jango.auth.jwt;

import com.jango.auth.entity.Role;
import com.jango.auth.entity.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JsonWebTokenFactory {

    @Autowired
    private JsonWebTokenConfig jsonWebTokenConfig;

    public JsonWebToken createJwtForUser(User user) {
        String jwt = Jwts.builder()
                         .setSubject(user.getEmail())
                         .claim("authorities", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                         .setIssuedAt(new Date(System.currentTimeMillis()))
                         .setExpiration(new Date(System.currentTimeMillis() + jsonWebTokenConfig.getJwtValidityTime()))
                         .signWith(jsonWebTokenConfig.getSignatureAlgorithm(), jsonWebTokenConfig.getSecretKey())
                         .compact();

        return new JsonWebToken(jwt);
    }

}
