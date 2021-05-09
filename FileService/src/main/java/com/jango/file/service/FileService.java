package com.jango.file.service;

import com.jango.file.client.AuthServiceClient;
import com.jango.file.client.UserServiceClient;
import com.jango.file.dto.FileMetaDataResponse;
import com.jango.file.dto.UploadFileMetaDataRequestPart;
import com.jango.file.dto.UserDetailsWithIdResponse;
import com.jango.file.entity.FileKey;
import com.jango.file.entity.FileMetaData;
import com.jango.file.entity.User;
import com.jango.file.repository.FileKeyRepository;
import com.jango.file.repository.FileMetaDataRepository;
import com.jango.file.repository.FileStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    
    public boolean uploadFile(UploadFileMetaDataRequestPart metaDataRequestPart, MultipartFile file) {
        
        String metaDataFileName = metaDataRequestPart.getFileName();
        String fileOriginalName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName = metaDataFileName.equals(EMPTY_FILE_NAME) == false ? metaDataFileName : fileOriginalName;

        UserDetailsWithIdResponse userDetails = userServiceClient.getUserDetailsByEmail(metaDataRequestPart.getOwnerEmail());
        
        User userWithId = User.builder()
                              .id(userDetails.getId())
                              .build();
        
        FileMetaData fileMetaData = FileMetaData.builder()
                                                .fileName(fileName)
                                                .description(metaDataRequestPart.getDescription())
                                                .owner(userWithId)
                                                .size(file.getSize())
                                                .publicFileFlag(metaDataRequestPart.getPublicFileFlag())
                                                .creationDate(Timestamp.valueOf(LocalDateTime.now()))
                                                .build();

        
        FileMetaData savedMetaData = fileMetaDataRepository.save(fileMetaData);
        
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
    
    public ByteArrayResource downloadFile(String key) {
        try {
            byte[] fileContent = fileStorageRepository.downloadFile(key);
            return new ByteArrayResource(fileContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public FileMetaDataResponse getFileMetaDataResponseByKey(String key) {
        
        FileMetaData fileMetaData = getFileMetaDataByKey(key);
        
        if(fileMetaData == null) {
            return null;
        }
        
        User owner = fileMetaData.getOwner();
        
        
        return FileMetaDataResponse.builder()
                                   .ownerEmail(owner.getEmail())
                                   .ownerUserName(owner.getName())
                                   .fileName(fileMetaData.getFileName())
                                   .fileDescription(fileMetaData.getDescription())
                                   .fileKey(key)
                                   .creationDate(fileMetaData.getCreationDate())
                                   .publicFileFlag(fileMetaData.getPublicFileFlag())
                                   .size(fileMetaData.getSize())
                                   .build();
    }
    
    public boolean removeFile(String key) { // TODO secure if user is ower or admin
        
        FileMetaData fileMetaData = getFileMetaDataByKey(key);
        
        if(fileMetaData == null) {
            return false;
        }
        
        fileMetaDataRepository.delete(fileMetaData);
        fileKeyRepository.deleteByKey(key);
        return fileStorageRepository.removeFile(key);
    }
    
    private FileMetaData getFileMetaDataByKey(String key) {
        
        Optional<FileKey> optionalFileKey = fileKeyRepository.findByKey(key);
        
        if(optionalFileKey.isEmpty()) {
            return null;
        }

        FileKey fileKey = optionalFileKey.get();

        Optional<FileMetaData> optionalFileMetaData = fileMetaDataRepository.findByKeyId(fileKey.getId());
        if(optionalFileMetaData.isEmpty()) {
            return null;
        }

        return optionalFileMetaData.get();
    }
    
    public List<FileMetaDataResponse> getFileListByOwner(String email, String token) {
        Boolean ownerOfToken = authServiceClient.isUserOwnerOfToken(email, token);
        
        UserDetailsWithIdResponse userDetails = userServiceClient.getUserDetailsByEmail(email);
        User owner = User.builder()
                         .id(userDetails.getId())
                         .build();
        
        List<FileMetaData> filesMetaData;
        
        if(ownerOfToken) {
          filesMetaData = fileMetaDataRepository.findAllByOwner(owner);
        } else {
            filesMetaData = fileMetaDataRepository.findAllByOwnerAndPublic(owner);
        }
        
        List<FileMetaDataResponse> fileMetaDataResponses = new ArrayList<>();
        
        for(FileMetaData fileMetaData: filesMetaData) {
            
            FileKey fileKey = fileKeyRepository.getOne(fileMetaData.getKeyId());
            
            FileMetaDataResponse response = FileMetaDataResponse.builder()
                                                                .ownerEmail(userDetails.getEmail())
                                                                .ownerUserName(userDetails.getUsername())
                                                                .fileName(fileMetaData.getFileName())
                                                                .fileDescription(fileMetaData.getDescription())
                                                                .fileKey(fileKey.getKey())
                                                                .publicFileFlag(fileMetaData.getPublicFileFlag())
                                                                .creationDate(fileMetaData.getCreationDate())
                                                                .size(fileMetaData.getSize())
                                                                .build();
            fileMetaDataResponses.add(response);
        }
        
        return fileMetaDataResponses;
    }
}
