package com.jango.file.repository;

import com.jango.file.entity.FileShare;
import com.jango.file.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileShareRepository extends JpaRepository<FileShare,Long> {

    public List<FileShare> findAllByRecipient(User recipient);

    public List<FileShare> findAllByOwner(User owner);
}
