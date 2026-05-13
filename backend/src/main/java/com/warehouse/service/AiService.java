package com.warehouse.service;

import com.warehouse.entity.Goods;
import com.warehouse.entity.Record;
import com.warehouse.entity.Storage;
import com.warehouse.mapper.GoodsMapper;
import com.warehouse.mapper.RecordMapper;
import com.warehouse.mapper.StorageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiService {

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.model:qwen-turbo}")
    private String model;

    @Value("${ai.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private StorageMapper storageMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    // 系统提示词
    private static final String SYSTEM_PROMPT =
        "你是一个专业的仓库管理系统AI助手。用户可以通过自然语言与系统交互。\n\n" +
        "系统功能：\n" +
        "1. 商品管理：查询商品、添加商品、修改商品、入库、出库\n" +
        "2. 仓库管理：查询仓库、添加仓库\n" +
        "3. 记录管理：查询出入库记录\n" +
        "4. 预警管理：库存预警\n\n" +
        "当用户询问数据时，你可以根据提供的实时数据回答。\n" +
        "请用简洁、专业的语言回复。";

    public String chat(String userMessage) {
        if (apiKey == null || apiKey.isEmpty()) {
            return getLocalResponse(userMessage);
        }
        return callAiApi(userMessage);
    }

    private String callAiApi(String userMessage) {
        try {
            String systemContext = SYSTEM_PROMPT + "\n\n当前仓库数据：\n" + getCurrentDataContext();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", Arrays.asList(
                Map.of("role", "system", "content", systemContext),
                Map.of("role", "user", "content", userMessage)
            ));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/chat/completions",
                HttpMethod.POST,
                request,
                Map.class
            );

            if (response.getBody() != null && response.getBody().containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            return "AI响应解析失败";
        } catch (Exception e) {
            return "AI服务调用失败: " + e.getMessage() + "\n\n" + getLocalResponse(userMessage);
        }
    }

    private String getLocalResponse(String userMessage) {
        String lowerMsg = userMessage.toLowerCase();
        String dataContext = getCurrentDataContext();

        if (lowerMsg.contains("库存") || lowerMsg.contains("商品")) {
            return buildInventoryResponse(userMessage, dataContext);
        } else if (lowerMsg.contains("仓库")) {
            return buildStorageResponse(dataContext);
        } else if (lowerMsg.contains("记录") || lowerMsg.contains("出入库")) {
            return buildRecordResponse();
        } else if (lowerMsg.contains("预警") || lowerMsg.contains("警告")) {
            return buildAlertResponse();
        } else if (lowerMsg.contains("报表") || lowerMsg.contains("报告") || lowerMsg.contains("统计")) {
            return buildReportResponse(dataContext);
        } else if (lowerMsg.contains("帮助") || lowerMsg.contains("help")) {
            return buildHelpResponse();
        } else {
            return "我理解您的请求。当前仓库数据如下：\n\n" + dataContext +
                   "\n\n您可以问我：\n" +
                   "- 库存情况\n" +
                   "- 仓库列表\n" +
                   "- 出入库记录\n" +
                   "- 库存预警\n" +
                   "- 生成报表\n" +
                   "- 帮助";
        }
    }

    private String getCurrentDataContext() {
        StringBuilder sb = new StringBuilder();
        List<Goods> goods = goodsMapper.selectList(null);
        sb.append("【商品总数】").append(goods.size()).append("种\n");

        int totalCount = goods.stream().filter(g -> g.getCount() != null).mapToInt(g -> g.getCount()).sum();
        sb.append("【总库存量】").append(totalCount).append("件\n\n");

        int lowStock = (int) goods.stream().filter(g -> g.getCount() != null && g.getCount() < 10).count();
        sb.append("【低库存商品】").append(lowStock).append("种\n\n");

        List<Storage> storages = storageMapper.selectList(null);
        sb.append("【仓库数量】").append(storages.size()).append("个\n");
        for (Storage s : storages) {
            sb.append("  - ").append(s.getName());
            if (s.getRemark() != null) {
                sb.append(": ").append(s.getRemark());
            }
            sb.append("\n");
        }

        if (lowStock > 0) {
            sb.append("\n【低库存商品明细】\n");
            goods.stream()
                .filter(g -> g.getCount() != null && g.getCount() < 10)
                .sorted(Comparator.comparing(g -> g.getCount()))
                .limit(10)
                .forEach(g -> sb.append("  - ").append(g.getName())
                    .append(": ").append(g.getCount()).append("件\n"));
        }

        return sb.toString();
    }

    private String buildInventoryResponse(String userMessage, String dataContext) {
        String lowerMsg = userMessage.toLowerCase();

        if (lowerMsg.contains("低于") || lowerMsg.contains("不足") || lowerMsg.contains("缺少")) {
            String[] parts = dataContext.split("【低库存商品明细】");
            return "以下是库存不足的商品（低于10件）：\n\n" + (parts.length > 0 ? parts[0] : dataContext);
        } else if (lowerMsg.contains("所有") || lowerMsg.contains("全部")) {
            return "当前所有商品库存情况：\n\n" + dataContext;
        } else {
            return "库存查询结果：\n\n" + dataContext +
                   "\n\n您可以这样问我：\n" +
                   "- 库存低于10的商品有哪些？\n" +
                   "- 所有商品的库存情况";
        }
    }

    private String buildStorageResponse(String dataContext) {
        String[] parts = dataContext.split("【仓库数量】");
        return "仓库信息：\n\n" + (parts.length > 1 ? parts[1] : "");
    }

    private String buildRecordResponse() {
        List<Record> records = recordMapper.selectList(null);
        return "出入库记录统计：\n" +
               "- 总记录数：" + records.size() + "条\n\n" +
               "您可以在【记录管理】页面查看详细记录。";
    }

    private String buildAlertResponse() {
        List<Goods> goods = goodsMapper.selectList(null);
        long alertCount = goods.stream()
            .filter(g -> g.getCount() != null && g.getCount() < 10)
            .count();

        return "库存预警状态：\n" +
               "- 低库存告警：" + alertCount + "种商品\n\n" +
               "请及时处理低库存商品，进行补货。\n" +
               "您可以在【库存预警】页面处理告警。";
    }

    private String buildReportResponse(String dataContext) {
        List<Goods> goods = goodsMapper.selectList(null);
        int totalCount = goods.stream().filter(g -> g.getCount() != null).mapToInt(g -> g.getCount()).sum();
        long lowStock = goods.stream().filter(g -> g.getCount() != null && g.getCount() < 10).count();
        long normalStock = goods.stream().filter(g -> g.getCount() != null && g.getCount() >= 10).count();
        List<Storage> storages = storageMapper.selectList(null);

        StringBuilder sb = new StringBuilder();
        sb.append("📊 库存分析报告\n\n");
        sb.append("=== 概览 ===\n");
        sb.append("• 商品种类：").append(goods.size()).append("种\n");
        sb.append("• 总库存量：").append(totalCount).append("件\n");
        sb.append("• 仓库数量：").append(storages.size()).append("个\n\n");
        sb.append("=== 库存状态 ===\n");
        sb.append("• 正常库存：").append(normalStock).append("种（≥10件）\n");
        sb.append("• 低库存：").append(lowStock).append("种（<10件）\n\n");
        sb.append("=== 风险提示 ===\n");
        if (lowStock > 0) {
            sb.append("⚠️ 存在").append(lowStock).append("种低库存商品，建议尽快补货\n\n");
        } else {
            sb.append("✓ 库存状态良好\n\n");
        }
        sb.append("=== 建议 ===\n");
        sb.append("1. 定期检查低库存商品\n");
        sb.append("2. 根据消耗速度设置合理库存阈值\n");
        sb.append("3. 做好采购计划，避免缺货\n\n");
        sb.append("如需详细报表，请访问【商品管理】页面导出Excel。");

        return sb.toString();
    }

    private String buildHelpResponse() {
        StringBuilder sb = new StringBuilder();
        sb.append("🤖 AI助手使用指南\n\n");
        sb.append("【支持的功能】\n\n");
        sb.append("1️⃣ 库存查询\n");
        sb.append("   - \"查询当前库存\"\n");
        sb.append("   - \"库存低于10的商品有哪些\"\n");
        sb.append("   - \"所有商品库存情况\"\n\n");
        sb.append("2️⃣ 仓库信息\n");
        sb.append("   - \"有哪些仓库\"\n");
        sb.append("   - \"仓库列表\"\n\n");
        sb.append("3️⃣ 记录查询\n");
        sb.append("   - \"查看出入库记录\"\n\n");
        sb.append("4️⃣ 预警查看\n");
        sb.append("   - \"有哪些预警\"\n\n");
        sb.append("5️⃣ 报表生成\n");
        sb.append("   - \"生成库存报表\"\n");
        sb.append("   - \"库存分析报告\"\n\n");
        sb.append("【操作提示】\n");
        sb.append("如需执行入库、出库等操作，请在对应管理页面进行。\n\n");
        sb.append("输入\"帮助\"可显示此指南。");
        return sb.toString();
    }

    public Map<String, Object> generateReport() {
        Map<String, Object> report = new HashMap<>();

        List<Goods> goods = goodsMapper.selectList(null);
        List<Storage> storages = storageMapper.selectList(null);
        List<Record> records = recordMapper.selectList(null);

        report.put("totalGoods", goods.size());
        report.put("totalStorage", storages.size());
        report.put("totalRecords", records.size());
        report.put("totalStock", goods.stream().filter(g -> g.getCount() != null).mapToInt(g -> g.getCount()).sum());

        List<Map<String, Object>> stockDistribution = new ArrayList<>();
        stockDistribution.add(createDistItem("充足（≥50）", goods.stream().filter(g -> g.getCount() != null && g.getCount() >= 50).count()));
        stockDistribution.add(createDistItem("正常（10-49）", goods.stream().filter(g -> g.getCount() != null && g.getCount() >= 10 && g.getCount() < 50).count()));
        stockDistribution.add(createDistItem("预警（1-9）", goods.stream().filter(g -> g.getCount() != null && g.getCount() >= 1 && g.getCount() < 10).count()));
        stockDistribution.add(createDistItem("缺货（0）", goods.stream().filter(g -> g.getCount() != null && g.getCount() == 0).count()));
        report.put("stockDistribution", stockDistribution);

        List<Map<String, Object>> storageStats = new ArrayList<>();
        for (Storage storage : storages) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("name", storage.getName());
            stat.put("goodsCount", goods.stream().filter(g -> Objects.equals(g.getStorage(), storage.getId())).count());
            stat.put("totalStock", goods.stream()
                .filter(g -> Objects.equals(g.getStorage(), storage.getId()) && g.getCount() != null)
                .mapToInt(g -> g.getCount())
                .sum());
            storageStats.add(stat);
        }
        report.put("storageStats", storageStats);

        List<Map<String, Object>> lowStockGoods = goods.stream()
            .filter(g -> g.getCount() != null && g.getCount() < 10)
            .sorted(Comparator.comparing(g -> g.getCount()))
            .map(g -> {
                Map<String, Object> item = new HashMap<>();
                item.put("name", g.getName());
                item.put("count", g.getCount());
                item.put("barcode", g.getBarcode());
                return item;
            })
            .collect(Collectors.toList());
        report.put("lowStockGoods", lowStockGoods);

        return report;
    }

    private Map<String, Object> createDistItem(String category, long count) {
        Map<String, Object> item = new HashMap<>();
        item.put("category", category);
        item.put("count", count);
        return item;
    }
}
