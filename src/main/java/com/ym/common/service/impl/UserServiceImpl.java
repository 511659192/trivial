package com.ym.common.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ym.common.dao.BaseMapper;
import com.ym.common.dao.UserMapper;
import com.ym.common.domain.User;
import com.ym.common.service.PasswordHelper;

@Service("userService2")
public class UserServiceImpl extends BaseServiceImpl<User> {

    @Resource(name = "passwordHelper2")
    private PasswordHelper passwordHelper;
}
