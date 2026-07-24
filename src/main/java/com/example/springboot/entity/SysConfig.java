package com.example.springboot.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统配置表 实体类。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sys_config")
public class SysConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID (字符串)
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private String id;

    /**
     * 配置键名
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 说明
     */
    private String remark;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

}