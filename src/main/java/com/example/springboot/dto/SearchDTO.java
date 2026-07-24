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
@Schema(description = "全文搜索请求")
public class SearchDTO {

    @NotBlank(message = "搜索关键词不能为空")
    @Schema(description = "搜索关键词", required = true)
    private String keyword;

    @Schema(description = "页码", example = "1")
    @Builder.Default
    private Integer page = 1;

    @Schema(description = "每页数量", example = "10")
    @Builder.Default
    private Integer pageSize = 10;
}