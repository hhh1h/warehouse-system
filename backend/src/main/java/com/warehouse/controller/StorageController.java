package com.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.entity.Storage;
import com.warehouse.mapper.StorageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageMapper storageMapper;

    @PostMapping
    public Result save(@RequestBody Storage storage) {
        // 检查仓库名称是否已存在
        QueryWrapper<Storage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", storage.getName());
        Storage existingStorage = storageMapper.selectOne(queryWrapper);
        if (existingStorage != null) {
            return Result.error("仓库名称已存在,请使用不同的名称");
        }

        storageMapper.insert(storage);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody Storage storage) {
        // 检查仓库名称是否与其他仓库重复
        QueryWrapper<Storage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", storage.getName())
                .ne("id", storage.getId());
        Storage existingStorage = storageMapper.selectOne(queryWrapper);
        if (existingStorage != null) {
            return Result.error("仓库名称已存在,请使用不同的名称");
        }

        storageMapper.updateById(storage);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        storageMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        QueryWrapper<Storage> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", search);
        Page<Storage> page = storageMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    @GetMapping("/list")
    public Result list() {
        return Result.success(storageMapper.selectList(null));
    }
}
