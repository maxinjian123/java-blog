package com.example.springboot.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisFlexConfig implements MyBatisFlexCustomizer {

    private static final Logger log = LoggerFactory.getLogger(MybatisFlexConfig.class);

    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(auditMessage ->
                log.info("SQL审计 -- 耗时: {}ms, SQL: {}", auditMessage.getElapsedTime(), auditMessage.getFullSql())
        );
    }
}