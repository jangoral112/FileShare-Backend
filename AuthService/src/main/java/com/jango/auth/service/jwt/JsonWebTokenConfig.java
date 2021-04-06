package com.jango.auth.service.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JsonWebTokenConfig {

    @Value("${security.jwt.validity-time}")
    private long JWT_VALIDITY_TIME;

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${security.jwt.signature-algorithm}")
    private SignatureAlgorithm SIGNATURE_ALGORITHM;

    public long getJwtValidityTime() {
        return JWT_VALIDITY_TIME;
    }

    public String getSecretKey() {
        return SECRET_KEY;
    }

    public SignatureAlgorithm getSignatureAlgorithm() {
        return SIGNATURE_ALGORITHM;
    }
}
