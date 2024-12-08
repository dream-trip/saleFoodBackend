package com.sale.uploadfile.controller;

import com.sale.uploadfile.entity.Result;
import com.sale.uploadfile.entity.User;
import com.sale.uploadfile.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public Result getUserInfo(@RequestParam int id){
        return Result.success(userService.getUserInfo(id));
    }
}
