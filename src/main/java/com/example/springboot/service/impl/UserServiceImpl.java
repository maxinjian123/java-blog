package com.example.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.example.springboot.common.exception.BusinessException;
import com.example.springboot.common.exception.ErrorCode;
import com.example.springboot.dto.LoginDTO;
import com.example.springboot.dto.UserProfileUpdateDTO;
import com.example.springboot.entity.User;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.UserService;
import com.example.springboot.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserVO login(LoginDTO loginDTO) {
        User user = findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }

        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }

        log.info("User logged in: userId={}, username={}", user.getId(), user.getUsername());
        return buildUserVO(user);
    }

    @Override
    public User findByUsername(String username) {
        return getOne(QueryWrapper.create().eq(User::getUsername, username));
    }

    @Override
    public UserVO getUserProfile(String userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        return buildUserVO(user);
    }

    @Override
    public boolean updateUserProfile(String userId, UserProfileUpdateDTO profileDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        BeanUtil.copyProperties(profileDTO, user, true);

        boolean updated = updateById(user);
        log.info("User profile updated: userId={}", userId);
        return updated;
    }

    private UserVO buildUserVO(User user) {
        return BeanUtil.copyProperties(user, UserVO.class);
    }
}