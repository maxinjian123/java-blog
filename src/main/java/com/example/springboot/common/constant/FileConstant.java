package com.example.springboot.common.constant;

public final class FileConstant {

    private FileConstant() {
    }

    public static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"};

    public static final String[] ALLOWED_DOC_TYPES = {"application/pdf", "text/plain", "text/markdown"};

    public static final String ALLOWED_EXTENSIONS = "jpg,jpeg,png,gif,webp,svg,pdf,txt,md";
}