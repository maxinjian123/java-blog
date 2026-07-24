package com.example.springboot.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页请求包装类
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    @Schema(description = "当前页号", defaultValue = "1")
    private int pageNum = 1;

    /**
     * 页面大小
     */

    @Schema(description = "页面大小", defaultValue = "10")
    private int pageSize = 10;


    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    @Schema(description = "排序顺序", defaultValue = "descend")
    private String sortOrder = "descend";
}
