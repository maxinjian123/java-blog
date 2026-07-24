package com.example.springboot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索结果分页")
public class SearchPageVO {

    @Schema(description = "搜索命中总数")
    private long total;

    @Schema(description = "当前页码，从1开始")
    private int page;

    @Schema(description = "每页数量")
    private int pageSize;

    @Schema(description = "文章搜索结果列表")
    private List<SearchVO> list;
}