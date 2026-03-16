package com.paymentsystem.user.service;

import com.paymentsystem.user.dto.UserRegisterRequest;
import com.paymentsystem.user.entity.User;

public interface UserService {

    void register(UserRegisterRequest request);

    User getById(Long id);
}
