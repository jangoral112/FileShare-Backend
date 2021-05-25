package com.jango.file.controller;

import com.jango.file.dto.FileMetaDataResponse;
import com.jango.file.dto.FileUploadMetadata;
import com.jango.file.mapping.JsonStringToPOJOMapper;
import com.jango.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController 
@RequestMapping("/file") 
public class FileController {

    @Autowired 
    private FileService fileService;

    @PostMapping (consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadFile(@RequestPart("metadata") String metaDataRequestPart, 
                             @RequestPart("file") MultipartFile file) {
        
        FileUploadMetadata metaDataPart = JsonStringToPOJOMapper.mapToFileMetaDataRequestPart(metaDataRequestPart);
        
        if(metaDataPart == null) {
            return ResponseEntity.status(400).body("Invalid file metadata"); // TODO exception
        }
        
        fileService.uploadFile(metaDataPart, file);

        return ResponseEntity.ok("Successfully uploaded file");
    }

    @GetMapping (produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<MultiValueMap<String, HttpEntity<?>>> downloadFile(@RequestParam("key") String key) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        FileMetaDataResponse fileMetaDataResponse = fileService.getFileMetaDataResponseByKey(key);
        ByteArrayResource byteArrayResource = fileService.downloadFile(key);

        builder.part("file_metadata", fileMetaDataResponse, MediaType.APPLICATION_JSON);
        builder.part("file_content", byteArrayResource.getByteArray(), MediaType.APPLICATION_OCTET_STREAM); // TODO replace byte array resource with byte[]

        return ResponseEntity.ok(builder.build());
    }
    
    @DeleteMapping
    public String removeFile(@RequestParam("key") String key) {
        
        if(fileService.removeFile(key)) {
            return "Successfully removed file";
        }
        
        return "Failed to remove file";
    }
    
    @GetMapping(path = "/list")
    public ResponseEntity<List<FileMetaDataResponse>> getFileListByOwner(@RequestParam(name = "ownerEmail") String ownerEmail, 
                                                                         @RequestHeader("authorization") String authToken) {
        
        List<FileMetaDataResponse> response = fileService.getFileListByOwner(ownerEmail, authToken);
        
        return ResponseEntity.ok(response);
    }
    
}
