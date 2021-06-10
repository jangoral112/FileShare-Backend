package com.jango.file.controller;

import com.jango.file.dto.FileShareRequest;
import com.jango.file.service.FileShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file/share")
public class FileShareController {

    @Autowired
    private FileShareService fileShareService;

    @PostMapping
    public ResponseEntity<String> shareFile(@RequestBody FileShareRequest fileShareRequest) {

        String response = fileShareService.shareFile(fileShareRequest);

        return ResponseEntity.ok(response);
    }
}
