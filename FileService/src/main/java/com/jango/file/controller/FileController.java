package com.jango.file.controller;

import com.jango.file.dto.FileMetadataResponse;
import com.jango.file.dto.FileUploadMetadata;
import com.jango.file.exception.InvalidRequestException;
import com.jango.file.mapping.JsonStringToPOJOMapper;
import com.jango.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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
            throw new InvalidRequestException("Metadata is in invalid format");
        }
        
        fileService.uploadFile(metaDataPart, file);

        return ResponseEntity.ok("Successfully uploaded file");
    }

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadFile(@RequestParam("key") String key,
                                               @RequestHeader("authorization") String authToken) {

        byte[] fileAsBytes = fileService.downloadFile(key, authToken);

        return ResponseEntity.ok(fileAsBytes);
    }
    
    @DeleteMapping
    public ResponseEntity<String> deleteFile(@RequestParam("key") String key,
                                             @RequestHeader("authorization") String authToken) {
        
        if(fileService.deleteFile(key, authToken)) {
            return ResponseEntity.ok("Successfully removed file");
        }
        
        return new ResponseEntity<>("Failed to remove file", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @GetMapping(path = "/metadata")
    public ResponseEntity<List<FileMetadataResponse>> getFileMetadataList(@RequestParam(name = "ownerEmail", required = false) String ownerEmail,
                                                                          @RequestParam(name = "privateFiles", required = false) Boolean privateFiles,
                                                                          @RequestHeader("authorization") String authToken) {

        List<FileMetadataResponse> response = fileService.getFileMetadataList(ownerEmail, privateFiles,authToken);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{key}/metadata")
    public ResponseEntity<FileMetadataResponse> getFileMetaDataByKey(@PathVariable(name = "key") String key,
                                                                     @RequestHeader("authorization") String authToken) {
        FileMetadataResponse fileMetadataResponse = fileService.getFileMetadataByKey(key, authToken);

        return ResponseEntity.ok(fileMetadataResponse);
    }
    
}
