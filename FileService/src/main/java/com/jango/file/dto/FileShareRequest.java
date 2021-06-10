package com.jango.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileShareRequest {

    private String ownerEmail;

    private String recipientEmail;

    private String fileKey;
}
