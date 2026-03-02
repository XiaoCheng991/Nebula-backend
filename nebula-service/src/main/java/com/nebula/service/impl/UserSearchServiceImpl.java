package com.nebula.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nebula.model.entity.SysUser;
import com.nebula.model.vo.UserVO;
import com.nebula.service.mapper.SysUserMapper;
import com.nebula.service.service.UserSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户搜索服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {

    private final SysUserMapper sysUserMapper;

    @Override
    public IPage<UserVO> searchUsers(String keyword, String onlineStatus, Integer pageNum, Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（用户名、昵称、邮箱）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getNickname, keyword)
                    .or()
                    .like(SysUser::getEmail, keyword));
        }

        // 在线状态筛选
        if (StringUtils.hasText(onlineStatus)) {
            // 需要通过UserProfile表查询，这里简化处理
            // 实际项目中可能需要联合查询
        }

        // 只查询启用状态的用户
        wrapper.eq(SysUser::getAccountStatus, 1);
        wrapper.orderByDesc(SysUser::getCreateTime);

        IPage<SysUser> userPage = sysUserMapper.selectPage(page, wrapper);

        // 转换为UserVO
        List<UserVO> voList = userPage.getRecords().stream()
                .map(this::convertToUserVO)
                .collect(Collectors.toList());

        Page<UserVO> resultPage = new Page<>(pageNum, pageSize, userPage.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    @Override
    public List<UserVO> searchUsersByKeyword(String keyword, Integer limit) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getNickname, keyword));
        }

        wrapper.eq(SysUser::getAccountStatus, 1);
        wrapper.orderByDesc(SysUser::getCreateTime);
        wrapper.last("LIMIT " + limit);

        List<SysUser> users = sysUserMapper.selectList(wrapper);

        return users.stream()
                .map(this::convertToUserVO)
                .collect(Collectors.toList());
    }

    @Override
    public UserVO getUserById(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        return convertToUserVO(user);
    }

    @Override
    public UserVO getUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = sysUserMapper.selectOne(wrapper);
        if (user == null) {
            return null;
        }
        return convertToUserVO(user);
    }

    /**
     * 转换为UserVO
     */
    private UserVO convertToUserVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatarUrl());
        vo.setStatus(user.getAccountStatus());
        vo.setAccountStatus(user.getAccountStatus());

        // 获取用户详细信息（从sys_users表已包含这些信息）
        vo.setBio(user.getBio());
        vo.setOnlineStatus(user.getOnlineStatus());
        vo.setLastSeenAt(user.getLastSeenAt());

        return vo;
    }
}
