package com.jango.file.repository;

import com.jango.file.entity.FileShare;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileShareRepository extends JpaRepository<FileShare,Long> {
}
