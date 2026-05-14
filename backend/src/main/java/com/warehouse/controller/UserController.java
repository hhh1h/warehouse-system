package com.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.JwtUtil;
import com.warehouse.common.Result;
import com.warehouse.entity.User;
import com.warehouse.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public Result save(@RequestBody User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        if (userMapper.selectOne(queryWrapper) != null) {
            return Result.error("用户名已存在");
        }
        // 强制设置默认角色为 user，不允许前端指定角色
        user.setRole("user");
        // 使用 BCrypt 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
        Page<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        if (user == null || user.getUsername() == null) {
            return Result.error("请求数据无效");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User loginUser = userMapper.selectOne(queryWrapper);
        if (loginUser == null) {
            return Result.error("用户不存在");
        }
        // 使用 BCrypt 验证密码
        if (!passwordEncoder.matches(user.getPassword(), loginUser.getPassword())) {
            return Result.error("密码错误");
        }

        String token = jwtUtil.generateToken(loginUser.getUsername(), loginUser.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", loginUser.getUsername());
        result.put("role", loginUser.getRole());
        return Result.success(result);
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        if (userMapper.selectOne(queryWrapper) != null) {
            return Result.error("用户名已存在");
        }

        // 强制设置默认角色为 user
        user.setRole("user");
        // 使用 BCrypt 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userMapper.insert(user);
        return Result.success("注册成功");
    }

    @GetMapping("/stats")
    public Result getStats() {
        List<User> users = userMapper.selectList(null);
        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", users.size());
        return Result.success(result);
    }
}
