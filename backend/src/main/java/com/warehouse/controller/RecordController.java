package com.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.entity.Record;
import com.warehouse.mapper.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/record")
public class RecordController {

    @Autowired
    private RecordMapper recordMapper;

    @GetMapping
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(required = false) String goods,
                              @RequestParam(required = false) String user) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("time");

        if (goods != null && !goods.isEmpty()) {
            queryWrapper.like("goods", goods);
        }
        if (user != null && !user.isEmpty()) {
            queryWrapper.like("user", user);
        }

        Page<Record> page = recordMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        recordMapper.deleteById(id);
        return Result.success();
    }
}
