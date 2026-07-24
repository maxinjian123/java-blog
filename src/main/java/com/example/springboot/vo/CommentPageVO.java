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
@Schema(description = "评论分页结果")
public class CommentPageVO {

    @Schema(description = "评论总数")
    private long total;

    @Schema(description = "当前页码，从1开始")
    private int page;

    @Schema(description = "每页数量")
    private int pageSize;

    @Schema(description = "顶级评论列表，每个评论内嵌子回复")
    private List<CommentVO> list;
}