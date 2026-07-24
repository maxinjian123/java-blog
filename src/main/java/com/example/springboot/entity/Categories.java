package com.example.springboot.entity;

import com.mybatisflex.annotation.Column;
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
 * 文章分类表 实体类。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("categories")
public class Categories implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID (字符串)
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private String id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类别名/URL拼音
     */
    private String slug;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 冗余字段：文章数量
     */
    private Long articleCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标识
     */
    @Column(isLogicDelete = true)
    private Integer deleted;

}