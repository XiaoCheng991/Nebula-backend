package com.nebula.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nebula.model.dto.UserDTO;
import com.nebula.model.entity.User;
import com.nebula.model.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 创建用户
     */
    Long createUser(UserDTO userDTO);

    /**
     * 更新用户
     */
    Boolean updateUser(UserDTO userDTO);

    /**
     * 删除用户
     */
    Boolean deleteUser(Long id);

    /**
     * 根据ID查询用户VO
     */
    UserVO getUserVO(Long id);

    /**
     * 查询所有用户
     */
    List<UserVO> listUsers();
}
