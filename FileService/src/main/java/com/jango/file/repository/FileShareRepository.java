package com.jango.file.repository;

import com.jango.file.entity.FileMetadata;
import com.jango.file.entity.FileShare;
import com.jango.file.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileShareRepository extends JpaRepository<FileShare,Long> {

    public List<FileShare> findAllByRecipient(User recipient);

    public List<FileShare> findAllByOwner(User owner);

    public Boolean existsByFileMetadataAndRecipient(FileMetadata fileMetadata, User recipient);

    public Optional<FileShare> findByFileMetadataAndRecipient(FileMetadata fileMetadata, User recipient);
}
