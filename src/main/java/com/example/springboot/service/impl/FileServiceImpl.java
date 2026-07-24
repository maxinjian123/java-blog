package com.example.springboot.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.springboot.common.constant.FileConstant;
import com.example.springboot.common.exception.BusinessException;
import com.example.springboot.common.exception.ErrorCode;
import com.example.springboot.service.FileService;
import com.example.springboot.service.MinioService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

    private static final long AVATAR_MAX_SIZE = 2 * 1024 * 1024;
    private static final long COVER_MAX_SIZE = 5 * 1024 * 1024;
    private static final String[] AVATAR_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};
    private static final String[] COVER_TYPES = {"image/jpeg", "image/png", "image/webp"};

    @Resource
    private MinioService minioService;

    @Override
    public String uploadImage(MultipartFile file, String userId) throws Exception {
        validateImage(file);
        String fileName = minioService.uploadFile(file, userId);
        return minioService.getPermanentUrl(fileName);
    }

    @Override
    public String uploadImage(MultipartFile file) throws Exception {
        return uploadImage(file, null);
    }

    @Override
    public String uploadDocument(MultipartFile file, String userId) throws Exception {
        validateDocument(file);
        String fileName = minioService.uploadFile(file, userId);
        return minioService.getPermanentUrl(fileName);
    }

    @Override
    public String uploadDocument(MultipartFile file) throws Exception {
        return uploadDocument(file, null);
    }

    @Override
    public String uploadAvatar(MultipartFile file, String userId) throws Exception {
        validateSizeAndType(file, AVATAR_MAX_SIZE, AVATAR_TYPES, "头像");
        String fileName = "avatars/" + (userId != null ? userId + "/" : StrUtil.EMPTY) + minioService.uploadFile(file, null);
        return minioService.getPermanentUrl(fileName);
    }

    @Override
    public String uploadCover(MultipartFile file, String userId) throws Exception {
        validateSizeAndType(file, COVER_MAX_SIZE, COVER_TYPES, "封面图");
        String fileName = "covers/" + (userId != null ? userId + "/" : StrUtil.EMPTY) + minioService.uploadFile(file, null);
        return minioService.getPermanentUrl(fileName);
    }

    @Override
    public void deleteFile(String fileName) throws Exception {
        minioService.deleteFile(fileName);
    }

    private void validateImage(MultipartFile file) {
        validateSizeAndType(file, FileConstant.MAX_FILE_SIZE, FileConstant.ALLOWED_IMAGE_TYPES, "图片");
    }

    private void validateDocument(MultipartFile file) {
        validateSizeAndType(file, FileConstant.MAX_FILE_SIZE, FileConstant.ALLOWED_DOC_TYPES, "文档");
    }

    private void validateSizeAndType(MultipartFile file, long maxSize, String[] allowedTypes, String typeName) {
        if (ObjectUtil.isNull(file) || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件不能为空");
        }
        if (file.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,
                    StrUtil.format("{}大小不能超过{}MB", typeName, maxSize / 1024 / 1024));
        }
        String contentType = file.getContentType();
        if (StrUtil.isBlank(contentType) || !ArrayUtil.contains(allowedTypes, contentType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,
                    StrUtil.format("不支持的{}类型，允许: {}", typeName, String.join(", ", allowedTypes)));
        }
    }
}