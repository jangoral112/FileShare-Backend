package com.jango.file.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jango.file.dto.FileUploadMetadata;

public class JsonStringToPOJOMapper {
    
    public static FileUploadMetadata mapToFileMetaDataRequestPart(String jsonRequestPart) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            
            return objectMapper.readValue(jsonRequestPart, FileUploadMetadata.class);
        
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
