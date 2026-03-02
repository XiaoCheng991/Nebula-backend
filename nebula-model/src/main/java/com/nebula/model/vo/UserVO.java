package com.nebula.model.vo;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 用户VO
 */
@Data
@Schema(description = "用户视图对象")
public class UserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "状态（0-禁用，1-启用）")
    private Integer status;

    @Schema(description = "账号状态（0-禁用，1-启用）")
    private Integer accountStatus;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "在线状态（online/offline/busy/away）")
    private String onlineStatus;

    @Schema(description = "最后活跃时间")
    private LocalDateTime lastSeenAt;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
