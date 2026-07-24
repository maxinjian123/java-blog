package com.example.springboot.service;

import com.example.springboot.dto.LoginDTO;
import com.example.springboot.dto.UserProfileUpdateDTO;
import com.example.springboot.entity.User;
import com.example.springboot.vo.UserVO;
import com.mybatisflex.core.service.IService;

public interface UserService extends IService<User> {

    UserVO login(LoginDTO loginDTO);

    User findByUsername(String username);

    UserVO getUserProfile(String userId);

    boolean updateUserProfile(String userId, UserProfileUpdateDTO profileDTO);
}