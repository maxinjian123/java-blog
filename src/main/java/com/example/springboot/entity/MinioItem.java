package com.example.springboot.entity;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class MinioItem {
    private String name;
    private String objectName;
    private boolean isDir;
    private String type;
    private Long size;
    private String lastModified;

    @Builder.Default
    private List<MinioItem> children = new ArrayList<>();
}