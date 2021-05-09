package com.jango.file.repository;

import com.jango.file.entity.FileMetaData;
import com.jango.file.entity.User;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetaData, Long> {
    
    public Optional<FileMetaData> findByKeyId(Long keyId);
    
    public List<FileMetaData> findAllByOwner(User owner);
    
    @Query(value = "SELECT f FROM FileMetaData f WHERE f.owner = ?1 AND f.publicFileFlag = TRUE")
    public List<FileMetaData> findAllByOwnerAndPublic(User owner);
}
