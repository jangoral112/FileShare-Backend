package com.jango.file.service;

import com.jango.file.client.AuthServiceClient;
import com.jango.file.client.UserServiceClient;
import com.jango.file.dto.*;
import com.jango.file.entity.FileKey;
import com.jango.file.entity.FileMetadata;
import com.jango.file.entity.FileShare;
import com.jango.file.entity.User;
import com.jango.file.exception.*;
import com.jango.file.repository.FileKeyRepository;
import com.jango.file.repository.FileMetadataRepository;
import com.jango.file.repository.FileShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileShareService {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private FileMetadataRepository fileMetaDataRepository;

    @Autowired
    private FileKeyRepository fileKeyRepository;

    @Autowired
    private FileShareRepository fileShareRepository;

    public String shareFile(FileShareRequest fileShareRequest) {

        if(fileShareRequest.getOwnerEmail().equals(fileShareRequest.getRecipientEmail())) {
            throw new SelfShareException("Invalid request, recipient can not be owner of given file");
        }

        UserDetailsWithIdResponse ownerDetails = userServiceClient.getUserDetailsByEmail(fileShareRequest.getOwnerEmail()); // TODO catch user not found
        UserDetailsWithIdResponse recipientDetails = userServiceClient.getUserDetailsByEmail(fileShareRequest.getRecipientEmail());
        FileMetadata fileMetadata = getFileMetaDataByFileKey(fileShareRequest.getFileKey());

        if(ownerDetails.getEmail().equals(fileMetadata.getOwner().getEmail()) == false) { // TODO fix no message
            throw new UserIsNotFileOwnerException("Given owner email differs from the one in file metadata");
        }

        User owner = User.builder()
                .id(ownerDetails.getId())
                .build();

        User recipient = User.builder()
                .id(recipientDetails.getId())
                .build();

        Boolean shareExist = fileShareRepository.existsByFileMetadataAndRecipient(fileMetadata, recipient);

        if(shareExist) {
            throw new FileShareAlreadyExistException("File is already shared");
        }

        FileShare fileShare = FileShare.builder()
                .owner(owner)
                .recipient(recipient)
                .fileMetadata(fileMetadata)
                .shareDate(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        fileShareRepository.save(fileShare);

        return "Successfully shared file";
    }


    public String deleteFileShare(DeleteFileShareRequest request) {

        UserDetailsWithIdResponse recipientDetails = userServiceClient.getUserDetailsByEmail(request.getRecipientEmail());

        FileMetadata fileMetadata = getFileMetaDataByFileKey(request.getFileKey());
        User recipient = User.builder()
                .id(recipientDetails.getId())
                .build();

        Optional<FileShare> optionalFileShare = fileShareRepository.findByFileMetadataAndRecipient(fileMetadata, recipient);

        if(optionalFileShare.isEmpty()) {
            throw new FileNotFoundException("File does not exist");
        }

        fileShareRepository.delete(optionalFileShare.get());

        return "Successfully deleted file";
    }

    private FileMetadata getFileMetaDataByFileKey(String key) { // TODO extract this common method from this class and FileService

        Optional<FileKey> optionalFileKey = fileKeyRepository.findByKey(key);
        if(optionalFileKey.isEmpty()) {
            throw new FileNotFoundException("File with given key does not exist");
        }

        FileKey fileKey = optionalFileKey.get();

        Optional<FileMetadata> optionalFileMetadata = fileMetaDataRepository.findByKeyId(fileKey.getId());
        if(optionalFileMetadata.isEmpty()) {
            throw new FileNotFoundException("File with given key does not exist");
        }

        return optionalFileMetadata.get();
    }

    public List<FileShareWithMetadataResponse> getReceiptedFileSharesMetadata(String recipientEmail, String authToken) {

        Boolean ownerOfToken = authServiceClient.isUserOwnerOfToken(recipientEmail, authToken);

        if(ownerOfToken == false) { // TODO if user is admin allow to get
            throw new UnauthorizedAccessException("Unauthorized access to receipted files list");
        }

        UserDetailsWithIdResponse recipientDetails = userServiceClient.getUserDetailsByEmail(recipientEmail);
        User recipient = User.builder()
                .id(recipientDetails.getId())
                .build();

        List<FileShare> receiptedFileShares = fileShareRepository.findAllByRecipient(recipient);

        List<FileShareWithMetadataResponse> response = new ArrayList<>();

        for(FileShare fileShare: receiptedFileShares) {

            FileMetadata fileMetadata = fileShare.getFileMetadata();

            Optional<FileKey> optionalFileKey = fileKeyRepository.findById(fileMetadata.getKeyId());
            if(optionalFileKey.isEmpty()) {
                throw new FileKeyDoesNotExistException("File key for given file could not be found");
            }

            FileMetadataResponse fileMetadataResponse = FileMetadataResponse.builder()
                    .ownerEmail(fileMetadata.getOwner().getEmail())
                    .ownerUsername(fileMetadata.getOwner().getUsername())
                    .fileName(fileMetadata.getFileName())
                    .fileDescription(fileMetadata.getDescription())
                    .fileKey(optionalFileKey.get().getKey())
                    .uploadTimestamp(fileMetadata.getUploadTimestamp())
                    .publicFileFlag(fileMetadata.getPublicFileFlag())
                    .size(fileMetadata.getSize())
                    .build();

            FileShareWithMetadataResponse fileShareWithMetadataResponse = FileShareWithMetadataResponse.builder()
                    .fileMetadataResponse(fileMetadataResponse)
                    .recipientEmail(recipientEmail)
                    .recipientUsername(fileShare.getRecipient().getUsername())
                    .shareTimestamp(fileShare.getShareDate())
                    .build();

            response.add(fileShareWithMetadataResponse);
        }

        return response;
    }

    public List<FileShareWithMetadataResponse> getFileSharesWithMetadata(String ownerEmail, String authToken) {

        Boolean ownerOfToken = authServiceClient.isUserOwnerOfToken(ownerEmail, authToken);

        if(ownerOfToken == false) { // TODO if user is admin allow to get
            throw new UnauthorizedAccessException("Unauthorized access to receipted files list");
        }

        UserDetailsWithIdResponse ownerDetails = userServiceClient.getUserDetailsByEmail(ownerEmail);
        User owner = User.builder()
                .id(ownerDetails.getId())
                .build();

        List<FileShare> ownedFileShares = fileShareRepository.findAllByOwner(owner);

        List<FileShareWithMetadataResponse> response = new ArrayList<>(); // TODO extract from here

        for(FileShare fileShare: ownedFileShares) {

            FileMetadata fileMetadata = fileShare.getFileMetadata();

            Optional<FileKey> optionalFileKey = fileKeyRepository.findById(fileMetadata.getKeyId());
            if(optionalFileKey.isEmpty()) {
                throw new FileKeyDoesNotExistException("File key for given file could not be found");
            }

            FileMetadataResponse fileMetadataResponse = FileMetadataResponse.builder()
                    .ownerEmail(fileMetadata.getOwner().getEmail())
                    .ownerUsername(fileMetadata.getOwner().getUsername())
                    .fileName(fileMetadata.getFileName())
                    .fileDescription(fileMetadata.getDescription())
                    .fileKey(optionalFileKey.get().getKey())
                    .uploadTimestamp(fileMetadata.getUploadTimestamp())
                    .publicFileFlag(fileMetadata.getPublicFileFlag())
                    .size(fileMetadata.getSize())
                    .build();

            FileShareWithMetadataResponse fileShareWithMetadataResponse = FileShareWithMetadataResponse.builder()
                    .fileMetadataResponse(fileMetadataResponse)
                    .recipientEmail(fileShare.getRecipient().getEmail())
                    .recipientUsername(fileShare.getRecipient().getUsername())
                    .shareTimestamp(fileShare.getShareDate())
                    .build();

            response.add(fileShareWithMetadataResponse);
        }

        return response;
    }

    public List<FileShareWithMetadataResponse> getFileShares(String authToken) {

        Boolean tokenOwnerIsAdmin = authServiceClient.parseTokenAuthorities(authToken)
                .stream()
                .anyMatch(roleName -> roleName.equals("ROLE_ADMIN"));

        if(tokenOwnerIsAdmin == false) {
            throw new UnauthorizedAccessException("Requested resource is only available for admin");
        }

        List<FileShare> fileShares = fileShareRepository.findAll();

        List<FileShareWithMetadataResponse> response = new ArrayList<>(); // TODO extract from here

        for(FileShare fileShare: fileShares) {

            FileMetadata fileMetadata = fileShare.getFileMetadata();

            Optional<FileKey> optionalFileKey = fileKeyRepository.findById(fileMetadata.getKeyId());
            if(optionalFileKey.isEmpty()) {
                throw new FileKeyDoesNotExistException("File key for given file could not be found");
            }

            FileMetadataResponse fileMetadataResponse = FileMetadataResponse.builder()
                    .ownerEmail(fileMetadata.getOwner().getEmail())
                    .ownerUsername(fileMetadata.getOwner().getUsername())
                    .fileName(fileMetadata.getFileName())
                    .fileDescription(fileMetadata.getDescription())
                    .fileKey(optionalFileKey.get().getKey())
                    .uploadTimestamp(fileMetadata.getUploadTimestamp())
                    .publicFileFlag(fileMetadata.getPublicFileFlag())
                    .size(fileMetadata.getSize())
                    .build();

            FileShareWithMetadataResponse fileShareWithMetadataResponse = FileShareWithMetadataResponse.builder()
                    .fileMetadataResponse(fileMetadataResponse)
                    .recipientEmail(fileShare.getRecipient().getEmail())
                    .recipientUsername(fileShare.getRecipient().getUsername())
                    .shareTimestamp(fileShare.getShareDate())
                    .build();

            response.add(fileShareWithMetadataResponse);
        }

        return response;
    }
}
