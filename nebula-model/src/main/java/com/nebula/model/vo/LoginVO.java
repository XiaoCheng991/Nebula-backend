package com.nebula.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginVO {

    @Schema(description = "访问令牌（Access Token，短期，30分钟）")
    private String token;

    @Schema(description = "刷新令牌（Refresh Token，长期，7天）")
    private String refreshToken;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID")
        private Long id;

        @Schema(description = "用户名")
        private String username;

        @Schema(description = "邮箱")
        private String email;

        @Schema(description = "昵称")
        private String nickname;

        @Schema(description = "头像")
        private String avatar;
    }
}
