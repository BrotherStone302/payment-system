package com.paymentsystem.user.controller;

import com.paymentsystem.common.result.Result;
import com.paymentsystem.user.dto.UserRegisterRequest;
import com.paymentsystem.user.entity.User;
import com.paymentsystem.user.service.UserService;
import com.paymentsystem.user.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterRequest request) {
        userService.register(request);
        return Result.success("注册成功", null);
    }

    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        User user = userService.getById(id);

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setPhone(user.getPhone());
        userVO.setCreateTime(user.getCreateTime());

        return Result.success(userVO);
    }
}