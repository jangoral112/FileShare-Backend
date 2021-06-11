package com.jango.file.service;

import com.jango.file.client.AuthServiceClient;
import com.jango.file.client.UserServiceClient;
import com.jango.file.dto.FileMetadataResponse;
import com.jango.file.dto.FileUploadMetadata;
import com.jango.file.dto.UserDetailsWithIdResponse;
import com.jango.file.entity.FileKey;
import com.jango.file.entity.FileMetadata;
import com.jango.file.entity.User;
import com.jango.file.exception.FileDownloadException;
import com.jango.file.exception.FileNotFoundException;
import com.jango.file.exception.UnauthorizedAccessException;
import com.jango.file.mapping.FileMetadataMapper;
import com.jango.file.repository.FileKeyRepository;
import com.jango.file.repository.FileMetaDataRepository;
import com.jango.file.repository.FileStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileService {
    
    private final String EMPTY_FILE_NAME = "";
    
    @Autowired
    private FileStorageRepository fileStorageRepository;
    
    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;
    
    @Autowired
    private FileKeyRepository fileKeyRepository;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private AuthServiceClient authServiceClient;
    
    public boolean uploadFile(FileUploadMetadata metaDataRequestPart, MultipartFile file) {
        
        String metaDataFileName = metaDataRequestPart.getFileName();
        String fileOriginalName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName = metaDataFileName.equals(EMPTY_FILE_NAME) == false ? metaDataFileName : fileOriginalName;

        UserDetailsWithIdResponse userDetails = userServiceClient.getUserDetailsByEmail(metaDataRequestPart.getOwnerEmail());
        
        User userWithId = User.builder()
                              .id(userDetails.getId())
                              .build();
        
        FileMetadata fileMetaData = FileMetadata.builder()
                                                .fileName(fileName)
                                                .description(metaDataRequestPart.getFileDescription())
                                                .owner(userWithId)
                                                .size(file.getSize())
                                                .publicFileFlag(metaDataRequestPart.getPublicFileFlag())
                                                .uploadTimestamp(Timestamp.valueOf(LocalDateTime.now()))
                                                .build();

        
        FileMetadata savedMetaData = fileMetaDataRepository.save(fileMetaData);

        Optional<FileKey> optionalFileKey = fileKeyRepository.findById(savedMetaData.getKeyId());
        if(optionalFileKey.isEmpty()) {
            fileMetaDataRepository.delete(savedMetaData);
            return false;
        }

        FileKey fileKey = optionalFileKey.get();

        try {
            fileStorageRepository.uploadFile(file, fileKey.getKey());
        } catch (Exception e) {
            fileMetaDataRepository.delete(savedMetaData);
            fileKeyRepository.delete(fileKey);
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public byte[] downloadFile(String key, String authToken) {

        FileMetadata fileMetaData = getFileMetadataByKey(key);

        if(fileMetaData == null) {
            throw new FileNotFoundException("File with given key does not exist!");
        }

        Boolean ownerOfToken = authServiceClient.isUserOwnerOfToken(fileMetaData.getOwner().getEmail(), authToken);

        if(ownerOfToken == false && fileMetaData.getPublicFileFlag() == false) { // TODO if user is admin allow to download
            throw new UnauthorizedAccessException("Unauthorized access to private file");
        }

        try {
            return fileStorageRepository.downloadFile(key);
        } catch (Exception e) {
            throw new FileDownloadException(e.getMessage());
        }
    }

    public FileMetadataResponse getFileMetadataByKey(String key, String authToken) {
        
        FileMetadata fileMetadata = getFileMetadataByKey(key);
        
        if(fileMetadata == null) {
            throw new FileNotFoundException("File with given key does not exist!");
        }
        
        User owner = fileMetadata.getOwner();
        Boolean ownerOfToken = authServiceClient.isUserOwnerOfToken(owner.getEmail(), authToken);

        if(ownerOfToken == false && fileMetadata.getPublicFileFlag() == false) { // TODO if user is admin allow to get data
            throw new UnauthorizedAccessException("Unauthorized access to private file");
        }
        
        return FileMetadataResponse.builder()
                                   .ownerEmail(owner.getEmail())
                                   .ownerUserName(owner.getUsername())
                                   .fileName(fileMetadata.getFileName())
                                   .fileDescription(fileMetadata.getDescription())
                                   .fileKey(key)
                                   .uploadTimestamp(fileMetadata.getUploadTimestamp())
                                   .publicFileFlag(fileMetadata.getPublicFileFlag())
                                   .size(fileMetadata.getSize())
                                   .build();
    }
    
    public boolean deleteFile(String key, String authToken) {
        
        FileMetadata fileMetaData = getFileMetadataByKey(key);
        
        if(fileMetaData == null) {
            throw new FileNotFoundException("File with given key does not exist!");
        }

        Boolean ownerOfToken = authServiceClient.isUserOwnerOfToken(fileMetaData.getOwner().getEmail(), authToken);

        if(ownerOfToken == false) { // TODO if user is admin allow to remove file
            throw new UnauthorizedAccessException("Unauthorized deletion of file");
        }
        
        fileMetaDataRepository.delete(fileMetaData);
        fileKeyRepository.deleteByKey(key);
        return fileStorageRepository.removeFile(key);
    }
    
    private FileMetadata getFileMetadataByKey(String key) {
        
        Optional<FileKey> optionalFileKey = fileKeyRepository.findByKey(key);
        
        if(optionalFileKey.isEmpty()) {
            return null;
        }

        FileKey fileKey = optionalFileKey.get();

        Optional<FileMetadata> optionalFileMetaData = fileMetaDataRepository.findByKeyId(fileKey.getId());
        if(optionalFileMetaData.isEmpty()) {
            return null;
        }

        return optionalFileMetaData.get();
    }
    
    public List<FileMetadataResponse> getFileMetadataList(String ownerEmail, Boolean privateFiles, String token) { // TODO allow admin to view all files both public and private

        List<FileMetadata> filesMetaData;

        if(ownerEmail != null) {

            UserDetailsWithIdResponse userDetails = userServiceClient.getUserDetailsByEmail(ownerEmail);
            User owner = User.builder()
                    .id(userDetails.getId())
                    .build();

            if(privateFiles != null && privateFiles == true) {
                Boolean ownerOfToken = authServiceClient.isUserOwnerOfToken(ownerEmail, token);

                if(ownerOfToken == false) {
                    throw new UnauthorizedAccessException("Unauthorized access to private files");
                }
                filesMetaData = fileMetaDataRepository.findAllByOwner(owner);
            } else {
                filesMetaData = fileMetaDataRepository.findAllByOwnerAndPublic(owner);
            }
        } else {
            filesMetaData = fileMetaDataRepository.findAllPublic();
        }

        return filesMetaData.stream().map(
                (fileMetadata) -> {
                    FileKey fileKey = fileKeyRepository.getOne(fileMetadata.getKeyId()); // TODO refactor FileMetadata entity to have FileKey field
                    return FileMetadataMapper.fileMetadataToResponse(fileMetadata, fileKey.getKey());
                })
                .collect(Collectors.toList());
    }
}
