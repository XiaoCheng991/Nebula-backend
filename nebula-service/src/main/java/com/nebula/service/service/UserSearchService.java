package com.nebula.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nebula.model.vo.UserVO;

import java.util.List;

/**
 * 用户搜索服务接口
 */
public interface UserSearchService {

    /**
     * 搜索用户
     *
     * @param keyword      关键词
     * @param onlineStatus 在线状态
     * @param pageNum      页码
     * @param pageSize     每页大小
     * @return 用户分页列表
     */
    IPage<UserVO> searchUsers(String keyword, String onlineStatus, Integer pageNum, Integer pageSize);

    /**
     * 根据关键词搜索用户
     *
     * @param keyword 关键词
     * @param limit   限制数量
     * @return 用户列表
     */
    List<UserVO> searchUsersByKeyword(String keyword, Integer limit);

    /**
     * 根据ID获取用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long userId);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserVO getUserByUsername(String username);
}
