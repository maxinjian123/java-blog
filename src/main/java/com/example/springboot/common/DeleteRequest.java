package com.example.springboot.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 删除请求包装类
 */
@Data
public class DeleteRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * id
     */

    @Schema(description = "id")
    private Long id;
}
