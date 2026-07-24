package com.example.springboot.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 每日访问统计表 实体类。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("daily_analytics")
public class DailyAnalytics implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 统计日期 (如 2026-03-31)
     */
    @Id
    private Date date;

    /**
     * PV访问量
     */
    private Long pv;

    /**
     * UV独立访客
     */
    private Long uv;

    private LocalDateTime createdAt;

}
