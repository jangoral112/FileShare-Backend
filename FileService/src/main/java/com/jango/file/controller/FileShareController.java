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

    @GetMapping
    public ResponseEntity<List<FileShareWithMetadataResponse>> getFileSharesWithMetadata(
                                                                    @RequestHeader("Authorization") String authToken) {

        List<FileShareWithMetadataResponse> response =
                fileShareService.getFileShares(authToken);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/recipient")
    public ResponseEntity<List<FileShareWithMetadataResponse>> getReceiptedFileSharesMetadata(
                                            @RequestParam(name = "recipientEmail") String recipientEmail,
                                            @RequestHeader("Authorization") String authToken) {

        List<FileShareWithMetadataResponse> response =
                fileShareService.getReceiptedFileSharesMetadata(recipientEmail, authToken);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<List<FileShareWithMetadataResponse>> getFileSharesWithMetadata(
                                            @RequestParam(name = "ownerEmail") String ownerEmail,
                                            @RequestHeader("Authorization") String authToken) {

        List<FileShareWithMetadataResponse> response =
                fileShareService.getFileSharesWithMetadata(ownerEmail, authToken);

        return ResponseEntity.ok(response);
    }

}
