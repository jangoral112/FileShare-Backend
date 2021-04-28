package com.jango.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadFileMetaDataRequestPart {
    
    private String ownerEmail;
    
    private String description;
    
    private String fileName;
    
    private Boolean publicFileFlag;

}
