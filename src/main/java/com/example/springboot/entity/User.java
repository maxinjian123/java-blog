package com.example.springboot.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户/管理员表 实体类。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator,value = KeyGenerators.snowFlakeId)
    private String id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String avatar;

    private String bio;

    private String role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(isLogicDelete = true)
    private Integer deleted;

}