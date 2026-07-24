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
@Schema(description = "修改分类请求（增量更新）")
public class CategoryUpdateDTO {

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类别名/URL拼音")
    private String slug;

    @Schema(description = "分类描述")
    private String description;
}