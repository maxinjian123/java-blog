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
 * 文章评论表 实体类。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("comments")
public class Comments implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 评论ID (字符串)
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private String id;

    /**
     * 关联的文章ID (字符串)
     */
    private String articleId;

    /**
     * 父评论ID (字符串，NULL表示顶级)
     */
    private String parentId;

    /**
     * 物化路径 (例如 /uuid-1/uuid-2/)
     */
    private String path;

    /**
     * 评论者昵称
     */
    private String nickname;

    /**
     * 评论者邮箱
     */
    private String email;

    /**
     * Gravatar 头像 MD5 虚拟生成列
     */
    private String emailMd5;

    /**
     * 个人网站
     */
    private String website;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论者IP
     */
    private String ip;

    /**
     * 设备信息
     */
    private String userAgent;

    /**
     * 是否为博主回复: 0-否, 1-是
     */
    private Boolean isAdmin;

    /**
     * 状态: 0-待审核, 1-已通过, 2-垃圾评论
     */
    private Integer status;

    /**
     * 评论时间
     */
    private LocalDateTime createdAt;

    /**
     * 逻辑删除标识
     */
    @Column(isLogicDelete = true)
    private Integer deleted;

}