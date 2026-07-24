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
@Schema(description = "PV/UV 日趋势数据点")
public class TrendVO {

    @Schema(description = "统计日期，格式 yyyy-MM-dd")
    private String date;

    @Schema(description = "页面浏览量（Page View）")
    private Long pv;

    @Schema(description = "独立访客数（Unique Visitor）")
    private Long uv;
}