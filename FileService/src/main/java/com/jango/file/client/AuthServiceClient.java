package com.jango.file.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="auth-service")
public interface AuthServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/validateOwner")
    public Boolean isUserOwnerOfToken(@RequestParam(value="email") String email,
                                      @RequestHeader(value="Authorization") String authToken);

    @RequestMapping(method = RequestMethod.GET, value = "/token/authorities")
    public List<String> parseTokenAuthorities(@RequestHeader(value="Authorization") String authToken);
}
