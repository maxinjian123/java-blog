package com.example.springboot.service;

import com.example.springboot.config.MinioProperties;
import com.example.springboot.entity.MinioItem;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MinioService {

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioProperties minioProperties;

    public void makeBucket(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
        );
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
            );
        }
    }

    public String uploadFile(MultipartFile file, String userId) throws Exception {
        String bucketName = minioProperties.getBucketName();
        makeBucket(bucketName);

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        if (userId != null) {
            fileName = "/" + userId + "/" + fileName;
        }
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        return fileName;
    }

    public String getPresignedUrl(String fileName, Integer expiry) throws Exception {
        String presignedObjectUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(minioProperties.getBucketName())
                        .object(fileName)
                        .expiry(expiry)
                        .build()
        );
        presignedObjectUrl = presignedObjectUrl.replace(":9000", "/minio/api");
        return presignedObjectUrl;
    }

    public String getPermanentUrl(String fileName) throws Exception {
        String bucketName = minioProperties.getBucketName();
        String endpoint = minioProperties.getEndpoint();
        if (!endpoint.endsWith("/")) {
            endpoint += "/";
        }
        endpoint = endpoint.replace(":9000", "/minio/api");
        return endpoint + bucketName + "/" + fileName;
    }

    public void deleteFile(String fileName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(fileName)
                        .build()
        );
    }

    public List<MinioItem> list(String prefix, boolean recursive) throws Exception {
        List<MinioItem> itemList = new ArrayList<>();
        ListObjectsArgs args = ListObjectsArgs.builder()
                .bucket(minioProperties.getBucketName())
                .prefix(prefix)
                .recursive(recursive)
                .build();

        Iterable<Result<Item>> results = minioClient.listObjects(args);
        for (Result<Item> result : results) {
            Item item = result.get();
            MinioItem minioItem = MinioItem.builder()
                    .name(item.objectName().substring(item.objectName().lastIndexOf("/") + 1))
                    .objectName(item.objectName())
                    .isDir(item.isDir())
                    .size(item.size())
                    .lastModified(item.lastModified() != null ? item.lastModified().toString() : null)
                    .build();
            itemList.add(minioItem);
        }
        return itemList;
    }
}