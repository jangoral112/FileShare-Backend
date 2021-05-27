package com.jango.file.repository;

import com.jango.file.entity.FileMetadata;
import com.jango.file.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetadata, Long> {

    public Optional<FileMetadata> findByKeyId(Long keyId);

    public List<FileMetadata> findAllByOwner(User owner);

    @Query(value = "SELECT f FROM FileMetadata f WHERE f.owner = ?1 AND f.publicFileFlag = TRUE")
    public List<FileMetadata> findAllByOwnerAndPublic(User owner);

    @Query(value = "SELECT f FROM FileMetadata f WHERE f.publicFileFlag = TRUE")
    public List<FileMetadata> findAllPublic();
}