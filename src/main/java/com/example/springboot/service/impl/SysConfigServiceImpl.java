package com.example.springboot.service.impl;

import com.example.springboot.dto.ConfigUpdateDTO;
import com.example.springboot.entity.SysConfig;
import com.example.springboot.mapper.SysConfigMapper;
import com.example.springboot.service.SysConfigService;
import com.example.springboot.vo.ConfigVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.springboot.entity.table.SysConfigTableDef.SYS_CONFIG;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private static final Logger log = LoggerFactory.getLogger(SysConfigServiceImpl.class);

    @Override
    public ConfigVO getPublicConfig() {
        Map<String, String> configMap = list(QueryWrapper.create()
                .where(SYS_CONFIG.CONFIG_KEY.in(
                        "blog_name", "blog_description", "announcement",
                        "icp", "logo", "favicon", "footer")))
                .stream()
                .collect(Collectors.toMap(SysConfig::getConfigKey, SysConfig::getConfigValue, (a, b) -> b));

        return ConfigVO.builder()
                .blogName(configMap.getOrDefault("blog_name", ""))
                .blogDescription(configMap.getOrDefault("blog_description", ""))
                .announcement(configMap.getOrDefault("announcement", ""))
                .icp(configMap.getOrDefault("icp", ""))
                .logo(configMap.getOrDefault("logo", ""))
                .favicon(configMap.getOrDefault("favicon", ""))
                .footer(configMap.getOrDefault("footer", ""))
                .build();
    }

    @Override
    @Transactional
    public void updateConfig(ConfigUpdateDTO updateDTO) {
        SysConfig existing = getOne(QueryWrapper.create()
                .where(SYS_CONFIG.CONFIG_KEY.eq(updateDTO.getConfigKey())));

        if (existing != null) {
            existing.setConfigValue(updateDTO.getConfigValue());
            if (updateDTO.getRemark() != null) {
                existing.setRemark(updateDTO.getRemark());
            }
            existing.setUpdatedAt(LocalDateTime.now());
            updateById(existing);
            log.info("Config updated: key={}, value={}", updateDTO.getConfigKey(), updateDTO.getConfigValue());
        } else {
            SysConfig config = SysConfig.builder()
                    .configKey(updateDTO.getConfigKey())
                    .configValue(updateDTO.getConfigValue())
                    .remark(updateDTO.getRemark())
                    .updatedAt(LocalDateTime.now())
                    .build();
            save(config);
            log.info("Config created: key={}, value={}", updateDTO.getConfigKey(), updateDTO.getConfigValue());
        }
    }
}