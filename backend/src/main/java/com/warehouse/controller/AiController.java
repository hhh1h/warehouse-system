package com.warehouse.controller;

import com.warehouse.common.Result;
import com.warehouse.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * AI对话接口
     */
    @PostMapping("/chat")
    public Result chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Result.error("消息不能为空");
        }
        String response = aiService.chat(message);
        return Result.success(response);
    }

    /**
     * AI报表生成接口
     */
    @GetMapping("/report")
    public Result generateReport() {
        Map<String, Object> report = aiService.generateReport();
        return Result.success(report);
    }
}
