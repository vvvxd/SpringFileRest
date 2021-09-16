package com.example.springfilerest.service;


import com.example.springfilerest.model.File;
import com.example.springfilerest.model.Status;
import com.example.springfilerest.model.User;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    void uploadFileToS3(MultipartFile file, User user);

    ResponseEntity<Resource> downloadFileFromS3(String filename, User user);

    void deleteFileFromS3Bucket(String filename, User user);

    List<File> getFilesFromS3Bucket(User user);

    List<File> getFilesFromDb(User user, Status status);

    File getById(Long id);

}
