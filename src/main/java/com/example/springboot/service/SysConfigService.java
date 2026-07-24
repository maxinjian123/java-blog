package com.example.springboot.service;

import com.example.springboot.dto.ConfigUpdateDTO;
import com.example.springboot.entity.SysConfig;
import com.example.springboot.vo.ConfigVO;
import com.mybatisflex.core.service.IService;

public interface SysConfigService extends IService<SysConfig> {

    ConfigVO getPublicConfig();

    void updateConfig(ConfigUpdateDTO updateDTO);
}