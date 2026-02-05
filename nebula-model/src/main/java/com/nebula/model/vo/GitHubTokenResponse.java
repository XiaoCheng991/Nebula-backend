package com.nebula.model.vo;

import lombok.Data;

/**
 * GitHub访问令牌响应
 */
@Data
public class GitHubTokenResponse {
    private String access_token;
    private String token_type;
    private String scope;
}
