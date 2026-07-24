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
@Schema(description = "全局公开配置（前台页面使用）")
public class ConfigVO {

    @Schema(description = "博客站点名称")
    private String blogName;

    @Schema(description = "博客站点描述")
    private String blogDescription;

    @Schema(description = "首页滚动公告")
    private String announcement;

    @Schema(description = "ICP备案号")
    private String icp;

    @Schema(description = "站点Logo图片URL")
    private String logo;

    @Schema(description = "站点Favicon图标URL")
    private String favicon;

    @Schema(description = "页脚自定义内容")
    private String footer;
}