package com.jango.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetaDataResponse {
    
    private String ownerEmail;
    
    private String ownerUserName;
    
    private String fileName;
    
    private String fileDescription;
    
    private String fileKey;
    
    private Boolean publicFileFlag;
    
    private Timestamp creationDate;
    
    private Long size;

}