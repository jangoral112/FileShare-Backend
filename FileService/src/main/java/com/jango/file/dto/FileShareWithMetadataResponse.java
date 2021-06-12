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
public class FileShareWithMetadataResponse {

    private String recipientEmail;

    private Timestamp shareTimestamp;

    private FileMetadataResponse fileMetadataResponse;
}
