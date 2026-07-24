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
@Schema(description = "评论分页查询条件")
public class CommentQueryDTO {

    @Schema(description = "页码", example = "1")
    @Builder.Default
    private Integer page = 1;

    @Schema(description = "每页数量", example = "20")
    @Builder.Default
    private Integer pageSize = 20;
}