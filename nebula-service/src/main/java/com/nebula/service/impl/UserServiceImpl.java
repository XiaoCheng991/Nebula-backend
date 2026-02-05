package com.nebula.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nebula.model.dto.UserDTO;
import com.nebula.model.entity.User;
import com.nebula.model.vo.UserVO;
import com.nebula.service.mapper.UserMapper;
import com.nebula.service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public Long createUser(UserDTO userDTO) {
        User user = BeanUtil.copyProperties(userDTO, User.class);

        // 密码加密
        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        save(user);
        return user.getId();
    }

    @Override
    public Boolean updateUser(UserDTO userDTO) {
        User user = BeanUtil.copyProperties(userDTO, User.class);

        // 如果有密码，则加密
        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        return updateById(user);
    }

    @Override
    public Boolean deleteUser(Long id) {
        return removeById(id);
    }

    @Override
    public UserVO getUserVO(Long id) {
        User user = getById(id);
        if (user == null) {
            return null;
        }
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    @Override
    public List<UserVO> listUsers() {
        List<User> users = list();
        return BeanUtil.copyToList(users, UserVO.class);
    }
}
