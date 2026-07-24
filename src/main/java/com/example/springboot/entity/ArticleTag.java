package com.example.springboot.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章与标签关联表 实体类。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("article_tag")
public class ArticleTag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID (字符串)
     */
    @Id
    private String articleId;

    /**
     * 标签ID (字符串)
     */
    @Id
    private String tagId;

}
