package com.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.warehouse.common.Result;
import com.warehouse.entity.User;
import com.warehouse.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping
    public Result save(@RequestBody User user) {
        userMapper.insert(user);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody User user) {
        userMapper.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        userMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", search);
        return Result.success(userMapper.selectList(queryWrapper));
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        List<User> list = userMapper.selectList(queryWrapper);
        if (list.isEmpty()) {
            return Result.error("用户名或密码错误");
        }
        User loginUser = list.get(0);
        return Result.success(loginUser);
    }

    @GetMapping("/stats")
    public Result getStats() {
        List<User> users = userMapper.selectList(null);
        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", users.size());
        return Result.success(result);
    }
}
