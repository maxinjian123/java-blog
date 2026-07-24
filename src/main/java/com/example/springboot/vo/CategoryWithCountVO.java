package com.example.springboot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分类信息（带文章数统计）")
public class CategoryWithCountVO {

    @Schema(description = "分类ID")
    private String id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类URL别名")
    private String slug;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "该分类下已发布的文章数量")
    private Long articleCount;

    @Schema(description = "创建时间 yyyy-MM-dd HH:mm:ss")
    private String createdAt;
}