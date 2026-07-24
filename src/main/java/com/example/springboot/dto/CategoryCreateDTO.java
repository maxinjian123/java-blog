package com.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建分类请求")
public class CategoryCreateDTO {

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", required = true)
    private String name;

    @Schema(description = "分类别名/URL拼音")
    private String slug;

    @Schema(description = "分类描述")
    private String description;
}