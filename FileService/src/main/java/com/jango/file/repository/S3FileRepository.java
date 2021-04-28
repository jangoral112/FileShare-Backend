package com.jango.file.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.amazonaws.waiters.WaiterParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Repository
public class S3FileRepository implements FileStorageRepository {
    
    private AmazonS3Client s3Client;
    
    private String bucketName;
    
    @Autowired
    public S3FileRepository(AmazonS3Client amazonS3Client, @Value("${default-bucket-name}") String bucketName) {
        this.bucketName = bucketName;
        this.s3Client = amazonS3Client;
        if(s3Client.doesBucketExist(bucketName) == false) {
            s3Client.createBucket(bucketName);
            s3Client.waiters().bucketExists().run(new WaiterParameters<>(
                    new HeadBucketRequest(bucketName)
            ));
        }
    }
    
    public void uploadFile(MultipartFile file, String key) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        s3Client.putObject(bucketName, key, file.getInputStream(), metadata);
    }
    
    public byte[] downloadFile(String key) throws IOException {
        S3Object s3Object = s3Client.getObject(bucketName, key);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        byte[] fileContent = IOUtils.toByteArray(inputStream);
        inputStream.close();
        return fileContent;
    }
    
    public boolean removeFile(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (AmazonServiceException e) {
            return false;
        }
        return true;
    }
    
    public boolean doesFileExist(String key) {
        try {
            return s3Client.doesObjectExist(bucketName, key);
        } catch (AmazonServiceException e) { // TODO throw new exception
            return false;
        }
    }
}
