package com.jango.file.controller;

import com.jango.file.dto.FileMetadataResponse;
import com.jango.file.dto.FileUploadMetadata;
import com.jango.file.exception.InvalidRequestException;
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

import java.security.InvalidAlgorithmParameterException;
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

    @GetMapping (path = "/{key}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<MultiValueMap<String, HttpEntity<?>>> downloadFile(@PathVariable("key") String key,
                                                                             @RequestHeader("authorization") String authToken) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        FileMetadataResponse fileMetaDataResponse = fileService.getFileMetadataByKey(key, authToken);
        ByteArrayResource byteArrayResource = fileService.downloadFile(key);

        builder.part("file_metadata", fileMetaDataResponse, MediaType.APPLICATION_JSON);
        builder.part("file_content", byteArrayResource.getByteArray(), MediaType.APPLICATION_OCTET_STREAM); // TODO replace byte array resource with byte[]

        return ResponseEntity.ok(builder.build());
    }
    
    @DeleteMapping(path = "/{key}")
    public String removeFile(@PathVariable("key") String key) {
        
        if(fileService.removeFile(key)) {
            return "Successfully removed file";
        }
        
        return "Failed to remove file";
    }
    
    @GetMapping(path = "/meta-data")
    public ResponseEntity<List<FileMetadataResponse>> getFileListByOwner(@RequestParam(name = "ownerEmail") String ownerEmail,
                                                                         @RequestHeader("authorization") String authToken) {
        
        List<FileMetadataResponse> response = fileService.getFileMetadataListByOwner(ownerEmail, authToken);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{key}/meta-data")
    public ResponseEntity<FileMetadataResponse> getFileMetaDataByKey(@PathVariable(name = "key") String key,
                                                                     @RequestHeader("authorization") String authToken) {
        FileMetadataResponse fileMetadataResponse = fileService.getFileMetadataByKey(key, authToken);

        return ResponseEntity.ok(fileMetadataResponse);
    }
    
}
