package com.jango.file.client;

import com.jango.file.dto.UserDetailsWithIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="user-service")
public interface UserServiceClient {
    
    @RequestMapping(method = RequestMethod.GET, value = "/user/private/details")
    public UserDetailsWithIdResponse getUserDetailsByEmail(@RequestParam(value="email") String email);

    @RequestMapping(method = RequestMethod.GET, value = "/user/private/details")
    public UserDetailsWithIdResponse getUserDetailsById(@RequestParam(value="userId") Long userId); // TODO remove if not used
}
