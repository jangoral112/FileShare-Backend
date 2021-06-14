package com.jango.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="file-service")
public interface FileServiceClient {

    @RequestMapping(method = RequestMethod.DELETE, value = "/file/user/{email}")
    public String deleteAllUsersFiles(@PathVariable String email,
                                      @RequestHeader(value="Authorization") String authToken);
}
