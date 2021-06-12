package com.jango.file.controller;

import com.jango.file.dto.FileShareRequest;
import com.jango.file.dto.FileShareWithMetadataResponse;
import com.jango.file.service.FileShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping(path = "/recipient")
    public ResponseEntity<List<FileShareWithMetadataResponse>> getReceiptedFilesMetadata(
                                            @RequestParam(name = "recipientEmail") String recipientEmail,
                                            @RequestHeader("authorization") String authToken) {

        List<FileShareWithMetadataResponse> response = fileShareService.getReceiptedFilesMetadata(recipientEmail,
                                                                                                  authToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<List<FileShareWithMetadataResponse>> getSharedFilesMetadata(
                                            @RequestParam(name = "ownerEmail") String ownerEmail,
                                            @RequestHeader("authorization") String authToken) {

        List<FileShareWithMetadataResponse> response = fileShareService.getSharesWithFilesMetadata(ownerEmail,
                                                                                                   authToken);

        return ResponseEntity.ok(response);
    }

}
