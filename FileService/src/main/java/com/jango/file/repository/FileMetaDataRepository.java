package com.jango.file.repository;

import com.jango.file.entity.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetaData, Long> {
    
    public Optional<FileMetaData> findByKeyId(Long keyId);

//    public List<FileMetaData> findAllByOwner // TODO add to file_metadata uploadDate and public flag
}
