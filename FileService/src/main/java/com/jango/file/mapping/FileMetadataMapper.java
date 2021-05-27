package com.jango.file.mapping;

import com.jango.file.dto.FileMetadataResponse;
import com.jango.file.entity.FileMetadata;

public class FileMetadataMapper {
    
    public static FileMetadataResponse fileMetadataToResponse(FileMetadata fileMetaData, String key) {
        return  FileMetadataResponse.builder()
                                    .ownerEmail(fileMetaData.getOwner().getEmail())
                                    .ownerUserName(fileMetaData.getOwner().getName())
                                    .fileName(fileMetaData.getFileName())
                                    .fileDescription(fileMetaData.getDescription())
                                    .fileKey(key)
                                    .publicFileFlag(fileMetaData.getPublicFileFlag())
                                    .uploadTimestamp(fileMetaData.getUploadTimestamp())
                                    .size(fileMetaData.getSize())
                                    .build();
    }
}
