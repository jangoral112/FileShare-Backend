package com.jango.file.service;

import com.jango.file.client.UserServiceClient;
import com.jango.file.dto.FileShareRequest;
import com.jango.file.dto.UserDetailsWithIdResponse;
import com.jango.file.entity.FileKey;
import com.jango.file.entity.FileMetadata;
import com.jango.file.entity.FileShare;
import com.jango.file.entity.User;
import com.jango.file.exception.FileNotFoundException;
import com.jango.file.exception.UserIsNotFileOwnerException;
import com.jango.file.repository.FileKeyRepository;
import com.jango.file.repository.FileMetaDataRepository;
import com.jango.file.repository.FileShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FileShareService {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;

    @Autowired
    private FileKeyRepository fileKeyRepository;

    @Autowired
    private FileShareRepository fileShareRepository;

    public String shareFile(FileShareRequest fileShareRequest) {

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

        FileShare fileShare = FileShare.builder()
                .owner(owner)
                .recipient(recipient)
                .fileMetadata(fileMetadata)
                .shareDate(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        fileShareRepository.save(fileShare); // TODO check if file is already shared, check if user is sharing file with himself

        return "Successfully uploaded file";
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
}
