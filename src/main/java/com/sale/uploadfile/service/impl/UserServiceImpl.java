package com.sale.uploadfile.service.impl;

import com.sale.uploadfile.entity.User;
import com.sale.uploadfile.mapper.UserMapper;
import com.sale.uploadfile.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User getUserInfo(int id) {
        return userMapper.getUserInfo(id);
    }
}
