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
@Schema(description = "创建标签请求")
public class TagCreateDTO {

    @NotBlank(message = "标签名称不能为空")
    @Schema(description = "标签名称", required = true)
    private String name;

    @Schema(description = "标签别名/URL拼音")
    private String slug;
}