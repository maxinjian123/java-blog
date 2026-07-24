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
@Schema(description = "分类信息")
public class CategoryVO {

    @Schema(description = "分类ID")
    private String id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类URL别名")
    private String slug;
}