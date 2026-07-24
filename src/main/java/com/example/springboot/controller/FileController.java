package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.common.exception.BusinessException;
import com.example.springboot.entity.MinioItem;
import com.example.springboot.service.FileService;
import com.example.springboot.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "文件管理", description = "图片/文档/头像/封面上传、下载、删除")
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    @Resource
    private MinioService minioService;

    @Operation(summary = "上传图片", description = "上传图片文件（jpg/png/gif/webp/svg ≤ 10MB），返回永久访问链接")
    @PostMapping("/upload/image")
    public BaseResponse<String> uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        String url = fileService.uploadImage(file);
        log.info("图片上传成功: {}", url);
        return ResultUtils.success(url);
    }

    @Operation(summary = "上传文档", description = "上传文档文件（pdf/txt/md ≤ 10MB），返回永久访问链接")
    @PostMapping("/upload/document")
    public BaseResponse<String> uploadDocument(@RequestParam("file") MultipartFile file) throws Exception {
        String url = fileService.uploadDocument(file);
        log.info("文档上传成功: {}", url);
        return ResultUtils.success(url);
    }

    @Operation(summary = "上传头像", description = "上传头像图片（jpg/png/gif/webp ≤ 2MB），返回永久访问链接")
    @PostMapping("/upload/avatar")
    public BaseResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file) throws Exception {
        String url = fileService.uploadAvatar(file, null);
        log.info("头像上传成功: {}", url);
        return ResultUtils.success(url);
    }

    @Operation(summary = "上传封面图", description = "上传封面图（jpg/png/webp ≤ 5MB），返回永久访问链接")
    @PostMapping("/upload/cover")
    public BaseResponse<String> uploadCover(@RequestParam("file") MultipartFile file) throws Exception {
        String url = fileService.uploadCover(file, null);
        log.info("封面图上传成功: {}", url);
        return ResultUtils.success(url);
    }

    @Operation(summary = "通用上传", description = "兼容旧接口，自动判断图片或文档类型")
    @PostMapping("/upload")
    public BaseResponse<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String url;
        try {
            url = fileService.uploadImage(file);
        } catch (BusinessException e) {
            url = fileService.uploadDocument(file);
        }
        log.info("文件上传成功: {}", url);
        return ResultUtils.success(url);
    }

    @Operation(summary = "获取文件URL", description = "根据文件名获取临时访问链接")
    @GetMapping("/url")
    public BaseResponse<String> getUrl(@RequestParam("fileName") String fileName) throws Exception {
        Integer expiry = 12 * 60 * 60;
        return ResultUtils.success(minioService.getPresignedUrl(fileName, expiry));
    }

    @Operation(summary = "删除文件", description = "根据文件名删除MinIO中的文件")
    @DeleteMapping("/delete")
    public BaseResponse<String> delete(@RequestParam("fileName") String fileName) throws Exception {
        fileService.deleteFile(fileName);
        return ResultUtils.success("删除成功");
    }

    @Operation(summary = "文件列表", description = "获取MinIO中的文件列表")
    @GetMapping("/list")
    public BaseResponse<List<MinioItem>> listFiles(
            @RequestParam(value = "prefix", required = false, defaultValue = "") String prefix,
            @RequestParam(value = "recursive", required = false, defaultValue = "true") boolean recursive) throws Exception {
        return ResultUtils.success(minioService.list(prefix, recursive));
    }
}