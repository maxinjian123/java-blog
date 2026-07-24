package com.example.springboot.service;

import com.example.springboot.vo.TrendingVO;

import java.util.List;

public interface TrendingService {

    List<TrendingVO> getTrending(String period, int limit);

    void incrementView(String articleId);

    void incrementLike(String articleId);

    void incrementComment(String articleId);

    void rebuildTrending();

    void removeFromTrending(String articleId);
}