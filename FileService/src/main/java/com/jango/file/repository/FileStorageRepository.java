package com.jango.file.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageRepository {

    public void uploadFile(MultipartFile file, String key) throws IOException;

    public byte[] downloadFile(String key) throws IOException;

    public boolean removeFile(String key);

    public boolean doesFileExist(String key);
}
