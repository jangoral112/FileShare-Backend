package com.jango.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsWithIdResponse {
    
    private Long id;
    
    private String username;
    
    private String email;
    
    private String description;
    
    private Timestamp creationDate;

}
