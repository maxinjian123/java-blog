package com.example.springboot.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String uploadImage(MultipartFile file, String userId) throws Exception;

    String uploadImage(MultipartFile file) throws Exception;

    String uploadDocument(MultipartFile file, String userId) throws Exception;

    String uploadDocument(MultipartFile file) throws Exception;

    String uploadAvatar(MultipartFile file, String userId) throws Exception;

    String uploadCover(MultipartFile file, String userId) throws Exception;

    void deleteFile(String fileName) throws Exception;
}