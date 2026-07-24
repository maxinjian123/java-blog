package com.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章列表查询条件")
public class ArticleQueryDTO {

    @Schema(description = "页码", example = "1")
    @Builder.Default
    private Integer page = 1;

    @Schema(description = "每页数量", example = "10")
    @Builder.Default
    private Integer pageSize = 10;

    @Schema(description = "分类ID")
    private String categoryId;

    @Schema(description = "标签ID")
    private String tagId;

    @Schema(description = "状态: 0-草稿, 1-已发布, 2-隐藏")
    @Builder.Default
    private Integer status = 1;

    @Schema(description = "游标分页: 上一页最后一条记录的ID")
    private String lastId;
}