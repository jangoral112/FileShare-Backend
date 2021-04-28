package com.jango.file.repository;

import com.jango.file.entity.FileKey;
import com.jango.file.entity.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FileKeyRepository extends JpaRepository<FileKey, Long> {
    
    public Optional<FileKey> findByKey(String key);
    
    @Transactional
    public Integer deleteByKey(String key);
}
