package com.jango.file.service;

import com.jango.file.client.AuthServiceClient;
import com.jango.file.client.UserServiceClient;
import com.jango.file.dto.FileMetadataResponse;
import com.jango.file.dto.FileUploadMetadata;
import com.jango.file.dto.UserDetailsWithIdResponse;
import com.jango.file.entity.FileKey;
import com.jango.file.entity.FileMetadata;
import com.jango.file.entity.User;
import com.jango.file.exception.*;
import com.jango.file.mapping.FileMetadataMapper;
import com.jango.file.repository.FileKeyRepository;
import com.jango.file.repository.FileMetadataRepository;
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
    private FileMetadataRepository fileMetadataRepository;
    
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

        
        FileMetadata savedMetaData = fileMetadataRepository.save(fileMetaData);

        Optional<FileKey> optionalFileKey = fileKeyRepository.findById(savedMetaData.getKeyId());
        if(optionalFileKey.isEmpty()) {
            fileMetadataRepository.delete(savedMetaData);
            return false;
        }

        FileKey fileKey = optionalFileKey.get();

        try {
            fileStorageRepository.uploadFile(file, fileKey.getKey());
        } catch (Exception e) {
            fileMetadataRepository.delete(savedMetaData);
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

        Boolean tokenOwnerIsAdmin = authServiceClient.parseTokenAuthorities(authToken)
                .stream()
                .anyMatch(roleName -> roleName.equals("ROLE_ADMIN"));

        if(ownerOfToken == false && fileMetadata.getPublicFileFlag() == false && tokenOwnerIsAdmin == false) {
            throw new UnauthorizedAccessException("Unauthorized access to private file");
        }

        return FileMetadataResponse.builder()
                                   .ownerEmail(owner.getEmail())
                                   .ownerUsername(owner.getUsername())
                                   .fileName(fileMetadata.getFileName())
                                   .fileDescription(fileMetadata.getDescription())
                                   .fileKey(key)
                                   .uploadTimestamp(fileMetadata.getUploadTimestamp())
                                   .publicFileFlag(fileMetadata.getPublicFileFlag())
                                   .size(fileMetadata.getSize())
                                   .build();
    }

    public boolean deleteFile(String key, String authToken) {

        FileMetadata fileMetadata = getFileMetadataByKey(key);

        if(fileMetadata == null) {
            throw new FileNotFoundException("File with given key does not exist!");
        }

        Boolean ownerOfToken = authServiceClient.isUserOwnerOfToken(fileMetadata.getOwner().getEmail(), authToken);

        Boolean tokenOwnerIsAdmin = authServiceClient.parseTokenAuthorities(authToken)
                .stream()
                .anyMatch(roleName -> roleName.equals("ROLE_ADMIN"));

        if(ownerOfToken == false && tokenOwnerIsAdmin == false) {
            throw new UnauthorizedAccessException("Unauthorized deletion of file");
        }

        fileMetadataRepository.delete(fileMetadata);
        fileKeyRepository.deleteByKey(key);
        return fileStorageRepository.removeFile(key);
    }

    public String deleteAllUsersFiles(String ownerEmail, String authToken) {

        Boolean tokenOwnerIsAdmin = authServiceClient.parseTokenAuthorities(authToken)
                .stream()
                .anyMatch(roleName -> roleName.equals("ROLE_ADMIN"));

        if(tokenOwnerIsAdmin == false) {
            throw new UnauthorizedAccessException("Unauthorized deletion of file");
        }

        UserDetailsWithIdResponse userDetails = userServiceClient.getUserDetailsByEmail(ownerEmail);
        User owner = User.builder()
                .id(userDetails.getId())
                .build();

        List<FileMetadata> userFiles = fileMetadataRepository.findAllByOwner(owner);

        for(FileMetadata fileMetadata: userFiles) {

            Optional<FileKey> optionalFileKey = fileKeyRepository.findById(fileMetadata.getKeyId());

            if(optionalFileKey.isEmpty()) {
                throw new FileKeyDoesNotExistException("File key does not exist");
            }

            FileKey fileKey = optionalFileKey.get();

            fileMetadataRepository.delete(fileMetadata);
            fileKeyRepository.deleteByKey(fileKey.getKey());
            if(false == fileStorageRepository.removeFile(fileKey.getKey())) {
                throw new FileDeletionFailedException("Failed to delete file");
            }
        }

        return "Successfully deleted all users files";
    }

    private FileMetadata getFileMetadataByKey(String key) {

        Optional<FileKey> optionalFileKey = fileKeyRepository.findByKey(key);

        if(optionalFileKey.isEmpty()) {
            return null;
        }

        FileKey fileKey = optionalFileKey.get();

        Optional<FileMetadata> optionalFileMetaData = fileMetadataRepository.findByKeyId(fileKey.getId());
        if(optionalFileMetaData.isEmpty()) {
            return null;
        }

        return optionalFileMetaData.get();
    }

    public List<FileMetadataResponse> getFileMetadataList(String ownerEmail, Boolean privateFiles, String authToken) {

        List<FileMetadata> filesMetaData;

        Boolean tokenOwnerIsAdmin = authServiceClient.parseTokenAuthorities(authToken)
                .stream()
                .anyMatch(roleName -> roleName.equals("ROLE_ADMIN"));

        if(ownerEmail != null) {

            UserDetailsWithIdResponse userDetails = userServiceClient.getUserDetailsByEmail(ownerEmail);
            User owner = User.builder()
                    .id(userDetails.getId())
                    .build();

            if(privateFiles != null && privateFiles == true) {
                Boolean ownerOfToken = authServiceClient.isUserOwnerOfToken(ownerEmail, authToken);

                if(ownerOfToken == false && tokenOwnerIsAdmin == false) {
                    throw new UnauthorizedAccessException("Unauthorized access to private files");
                }
                filesMetaData = fileMetadataRepository.findAllByOwner(owner);
            } else {
                filesMetaData = fileMetadataRepository.findAllByOwnerAndPublic(owner);
            }
        } else if(tokenOwnerIsAdmin && privateFiles) {
            filesMetaData = fileMetadataRepository.findAll();
        } else {
            filesMetaData = fileMetadataRepository.findAllPublic();
        }

        return filesMetaData.stream().map(
                (fileMetadata) -> {
                    FileKey fileKey = fileKeyRepository.getOne(fileMetadata.getKeyId()); // TODO refactor FileMetadata entity to have FileKey field
                    return FileMetadataMapper.fileMetadataToResponse(fileMetadata, fileKey.getKey());
                })
                .collect(Collectors.toList());
    }
}
