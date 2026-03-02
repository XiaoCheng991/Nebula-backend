package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户搜索DTO
 */
@Data
@Schema(description = "用户搜索条件")
public class UserSearchDTO {

    @Schema(description = "搜索关键词(用户名/昵称/邮箱)")
    private String keyword;

    @Schema(description = "在线状态(online-在线,offline-离线,busy-忙碌,away-离开)")
    private String onlineStatus;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "20")
    private Integer pageSize = 20;
}
