package com.jango.file.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="auth-service")
public interface AuthServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/validateOwner")
    public Boolean isUserOwnerOfToken(@RequestParam(value="email") String email, @RequestParam(value="token") String token);
    
}
