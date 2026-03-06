package com.nebula.model.vo;

import lombok.Data;

/**
 * GitHub访问令牌响应
 */
@Data
public class GitHubTokenResponse {
    private String accessToken;
    private String tokenType;
    private String scope;
}
