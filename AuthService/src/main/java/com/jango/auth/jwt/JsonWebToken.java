package com.jango.auth.jwt;

public class JsonWebToken {

    private String jwt;

    protected JsonWebToken(String jwt) {
        this.jwt = jwt;
    }

    public String asString() {
        return jwt;
    }
}
