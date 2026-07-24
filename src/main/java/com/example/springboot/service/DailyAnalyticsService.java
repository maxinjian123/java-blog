package com.example.springboot.service;

import com.example.springboot.entity.DailyAnalytics;
import com.example.springboot.vo.TrendVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

public interface DailyAnalyticsService extends IService<DailyAnalytics> {

    List<TrendVO> getTrend(int days);
}