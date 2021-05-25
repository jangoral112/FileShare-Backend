package com.jango.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadMetadata {
    
    private String ownerEmail;
    
    private String fileDescription;
    
    private String fileName;
    
    private Boolean publicFileFlag;

}
