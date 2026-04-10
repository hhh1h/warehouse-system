package com.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.warehouse.common.Result;
import com.warehouse.entity.Alert;
import com.warehouse.entity.Goods;
import com.warehouse.mapper.AlertMapper;
import com.warehouse.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alert")
public class AlertController {

    @Autowired
    private AlertMapper alertMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @GetMapping
    public Result getAlerts(@RequestParam(defaultValue = "unread") String status) {
        QueryWrapper<Alert> queryWrapper = new QueryWrapper<>();
        if ("unread".equals(status)) {
            queryWrapper.eq("status", "unread");
        }
        queryWrapper.orderByDesc("create_time");
        List<Alert> alerts = alertMapper.selectList(queryWrapper);
        return Result.success(alerts);
    }

    @GetMapping("/count")
    public Result getUnreadCount() {
        QueryWrapper<Alert> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "unread");
        Integer count = Math.toIntExact(alertMapper.selectCount(queryWrapper));
        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", count);
        return Result.success(result);
    }

    @PutMapping("/{id}/read")
    public Result markAsRead(@PathVariable Integer id) {
        Alert alert = alertMapper.selectById(id);
        if (alert != null) {
            alert.setStatus("read");
            alert.setReadTime(new Date());
            alertMapper.updateById(alert);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteAlert(@PathVariable Integer id) {
        alertMapper.deleteById(id);
        return Result.success();
    }

    @PostMapping("/check")
    public Result checkInventoryAlerts() {
        List<Goods> goodsList = goodsMapper.selectList(null);
        int alertCount = 0;

        for (Goods goods : goodsList) {
            int currentCount = goodsMapper.getCount(goods.getId());
            int lowThreshold = 10;

            if (currentCount < lowThreshold) {
                QueryWrapper<Alert> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("goods_id", goods.getId())
                        .eq("status", "unread");
                Long existingAlert = alertMapper.selectCount(queryWrapper);

                if (existingAlert == 0) {
                    Alert alert = new Alert();
                    alert.setType("low_stock");
                    alert.setGoodsName(goods.getName());
                    alert.setGoodsId(goods.getId());
                    alert.setCurrentCount(currentCount);
                    alert.setThreshold(lowThreshold);
                    alert.setStorageId(goods.getStorage());
                    alert.setStatus("unread");
                    alert.setCreateTime(new Date());
                    alertMapper.insert(alert);
                    alertCount++;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("alertCount", alertCount);
        return Result.success(result);
    }
}
