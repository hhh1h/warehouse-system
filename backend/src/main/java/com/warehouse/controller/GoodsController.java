package com.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.entity.Goods;
import com.warehouse.entity.Record;
import com.warehouse.entity.Storage;
import com.warehouse.entity.User;
import com.warehouse.mapper.GoodsMapper;
import com.warehouse.mapper.RecordMapper;
import com.warehouse.mapper.StorageMapper;
import com.warehouse.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private StorageMapper storageMapper;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping
    public Result save(@RequestBody Goods goods) {
        // 先重置 AUTO_INCREMENT 到当前最大 ID + 1
        Integer maxId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM `goods`", Integer.class);
        if (maxId != null) {
            jdbcTemplate.update("ALTER TABLE `goods` AUTO_INCREMENT = ?", maxId + 1);
        } else {
            jdbcTemplate.update("ALTER TABLE `goods` AUTO_INCREMENT = 1");
        }

        // 使用原生 SQL 插入，完全绕过 MyBatis-Plus 的 ID 生成
        String sql = "INSERT INTO `goods` (`name`, `storage`, `type`, `remark`) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, goods.getName(), goods.getStorage(), goods.getType(), goods.getRemark());
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody Goods goods) {
        goodsMapper.updateById(goods);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        goodsMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", search);
        Page<Goods> goodsPage = goodsMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);

        List<Goods> records = goodsPage.getRecords();
        for (Goods goods : records) {
            if (goods.getStorage() != null) {
                Storage storage = storageMapper.selectById(goods.getStorage());
                if (storage != null) {
                    goods.setRemark(storage.getName());
                }
            }
        }

        return Result.success(goodsPage);
    }

    @DeleteMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        goodsMapper.deleteBatchIds(ids);
        return Result.success();
    }

    @PostMapping("/inbound")
    public Result inbound(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer goodsId = (Integer) params.get("goodsId");
        Integer count = (Integer) params.get("count");
        String user = (String) params.get("user");

        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            return Result.error("商品不存在");
        }

        goodsMapper.inbound(goodsId, count);

        Record record = new Record();
        record.setGoods(goods.getName());
        record.setGoodsId(goodsId);
        record.setUser(user);
        record.setCount(count);
        record.setStorage(goods.getStorage());
        record.setTime(new java.util.Date());
        recordMapper.insert(record);

        return Result.success();
    }

    @PostMapping("/outbound")
    public Result outbound(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer goodsId = (Integer) params.get("goodsId");
        Integer count = (Integer) params.get("count");
        String user = (String) params.get("user");

        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            return Result.error("商品不存在");
        }

        boolean success = goodsMapper.outbound(goodsId, count);
        if (!success) {
            return Result.error("库存不足");
        }

        Record record = new Record();
        record.setGoods(goods.getName());
        record.setGoodsId(goodsId);
        record.setUser(user);
        record.setCount(-count);
        record.setStorage(goods.getStorage());
        record.setTime(new java.util.Date());
        recordMapper.insert(record);

        return Result.success();
    }

    @GetMapping("/storage")
    public Result getStorageList() {
        List<Storage> list = storageMapper.selectList(null);
        return Result.success(list);
    }

    @GetMapping("/barcode/{barcode}")
    public Result getByBarcode(@PathVariable String barcode) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("barcode", barcode);
        Goods goods = goodsMapper.selectOne(queryWrapper);
        if (goods == null) {
            return Result.error("未找到该条码商品");
        }
        return Result.success(goods);
    }

    @GetMapping("/stats")
    public Result getStats() {
        List<Goods> goodsList = goodsMapper.selectList(null);
        List<Storage> storageList = storageMapper.selectList(null);

        Map<String, Object> result = new HashMap<>();
        result.put("totalGoods", goodsList.size());
        result.put("totalStorage", storageList.size());

        int totalCount = 0;
        for (Goods goods : goodsList) {
            totalCount += goodsMapper.getCount(goods.getId());
        }
        result.put("totalCount", totalCount);

        return Result.success(result);
    }

    @PostMapping("/reset-auto-increment")
    public Result resetAutoIncrement() {
        // 重置 AUTO_INCREMENT 为 21（假设当前最大 ID 是 20）
        jdbcTemplate.update("ALTER TABLE `goods` AUTO_INCREMENT = 21");
        return Result.success("AUTO_INCREMENT 已重置为 21");
    }
}
