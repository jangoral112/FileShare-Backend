package com.jango.gateway.filter;

import com.jango.gateway.jwt.JsonWebTokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthorizationFilterFactory extends AbstractGatewayFilterFactory<AuthorizationFilterFactory.Configuration> {

    @Autowired
    private JsonWebTokenProperties jwtProperties;
    
    public AuthorizationFilterFactory() {
        super(Configuration.class);
    }

    @Override
    public String name() {
        return "Authorization";
    }
    
    @Override 
    public GatewayFilter apply(Configuration configuration) {
        return new AuthorizationFilter(jwtProperties, configuration);
    }

    @Override 
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("requiredRoles");
    }

    public static class Configuration {
        private String[] requiredRoles;
        
        public Configuration() {}

        public String[] getRequiredRoles() {
            return requiredRoles;
        }

        public void setRequiredRoles(String... requiredRoles) {
            this.requiredRoles = requiredRoles;
        }
    }
}
