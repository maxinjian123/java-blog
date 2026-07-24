package com.example.springboot.service.impl;

import com.example.springboot.entity.DailyAnalytics;
import com.example.springboot.mapper.DailyAnalyticsMapper;
import com.example.springboot.service.DailyAnalyticsService;
import com.example.springboot.vo.TrendVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.springboot.entity.table.DailyAnalyticsTableDef.DAILY_ANALYTICS;

@Service
public class DailyAnalyticsServiceImpl extends ServiceImpl<DailyAnalyticsMapper, DailyAnalytics> implements DailyAnalyticsService {

    @Override
    public List<TrendVO> getTrend(int days) {
        int maxDays = Math.min(days, 30);
        LocalDate startDate = LocalDate.now().minusDays(maxDays - 1);

        List<DailyAnalytics> list = list(QueryWrapper.create()
                .where(DAILY_ANALYTICS.DATE.ge(Date.valueOf(startDate)))
                .orderBy(DAILY_ANALYTICS.DATE, true));

        return list.stream()
                .map(a -> TrendVO.builder()
                        .date(a.getDate().toString())
                        .pv(a.getPv() != null ? a.getPv() : 0L)
                        .uv(a.getUv() != null ? a.getUv() : 0L)
                        .build())
                .collect(Collectors.toList());
    }
}