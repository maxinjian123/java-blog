package com.example.springboot.service;

import com.example.springboot.vo.RecommendVO;

import java.util.List;

public interface RecommendService {

    List<RecommendVO> getRecommend(String articleId, int limit);

    void refreshRecommend(String articleId);

    void removeRecommend(String articleId);
}