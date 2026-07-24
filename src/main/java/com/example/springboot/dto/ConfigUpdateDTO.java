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
@Schema(description = "修改系统配置项请求")
public class ConfigUpdateDTO {

    @NotBlank(message = "配置键名不能为空")
    @Schema(description = "配置键名", example = "blog_name")
    private String configKey;

    @NotBlank(message = "配置值不能为空")
    @Schema(description = "配置值", example = "我的博客")
    private String configValue;

    @Schema(description = "说明")
    private String remark;
}