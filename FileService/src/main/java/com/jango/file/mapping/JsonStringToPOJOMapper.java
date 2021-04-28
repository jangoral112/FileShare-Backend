package com.jango.file.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jango.file.dto.UploadFileMetaDataRequestPart;

public class JsonStringToPOJOMapper {
    
    public static UploadFileMetaDataRequestPart mapToFileMetaDataRequestPart(String jsonRequestPart) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            
            return objectMapper.readValue(jsonRequestPart, UploadFileMetaDataRequestPart.class);
        
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
