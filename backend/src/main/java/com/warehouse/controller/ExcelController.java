package com.warehouse.controller;

import com.warehouse.common.Result;
import com.warehouse.entity.Goods;
import com.warehouse.entity.Storage;
import com.warehouse.mapper.GoodsMapper;
import com.warehouse.mapper.StorageMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private StorageMapper storageMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 批量导入商品
     */
    @PostMapping("/import/goods")
    public Result importGoods(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
                return Result.error("请上传Excel文件");
            }

            // 获取所有仓库信息
            List<Storage> storageList = storageMapper.selectList(null);
            Map<String, Integer> storageNameToId = new HashMap<>();
            for (Storage s : storageList) {
                storageNameToId.put(s.getName(), s.getId());
            }

            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            
            int successCount = 0;
            int failCount = 0;
            List<String> errors = new ArrayList<>();

            // 跳过表头，从第二行开始
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String name = getCellValue(row.getCell(0));
                    String storageName = getCellValue(row.getCell(1));
                    String typeStr = getCellValue(row.getCell(2));
                    String barcode = getCellValue(row.getCell(3));
                    String remark = getCellValue(row.getCell(4));

                    if (name == null || name.trim().isEmpty()) {
                        errors.add("第" + (i + 1) + "行: 商品名称不能为空");
                        failCount++;
                        continue;
                    }

                    Integer storageId = null;
                    if (storageName != null && !storageName.trim().isEmpty()) {
                        storageId = storageNameToId.get(storageName.trim());
                        if (storageId == null) {
                            errors.add("第" + (i + 1) + "行: 仓库'" + storageName + "'不存在");
                            failCount++;
                            continue;
                        }
                    }

                    int type = 0;
                    if (typeStr != null && !typeStr.trim().isEmpty()) {
                        try {
                            type = Integer.parseInt(typeStr.trim());
                        } catch (NumberFormatException e) {
                            errors.add("第" + (i + 1) + "行: 库存数量格式错误");
                            failCount++;
                            continue;
                        }
                    }

                    // 插入数据库
                    String sql = "INSERT INTO `goods` (`name`, `storage`, `type`, `remark`, `barcode`) VALUES (?, ?, ?, ?, ?)";
                    jdbcTemplate.update(sql, name.trim(), storageId, type, remark, barcode);
                    successCount++;

                } catch (Exception e) {
                    errors.add("第" + (i + 1) + "行: " + e.getMessage());
                    failCount++;
                }
            }

            workbook.close();

            String message = "导入完成: 成功 " + successCount + " 条";
            if (failCount > 0) {
                message += ", 失败 " + failCount + " 条";
            }
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("successCount", successCount);
            resultMap.put("failCount", failCount);
            resultMap.put("errors", errors);
            resultMap.put("message", message);
            
            return Result.success(resultMap);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 导出商品Excel
     */
    @GetMapping("/export/goods")
    public void exportGoods(HttpServletResponse response) {
        try {
            List<Goods> goodsList = goodsMapper.selectList(null);
            List<Storage> storageList = storageMapper.selectList(null);
            
            // 构建仓库ID到名称的映射
            Map<Integer, String> storageIdToName = new HashMap<>();
            for (Storage s : storageList) {
                storageIdToName.put(s.getId(), s.getName());
            }

            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("商品列表");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "商品名称", "仓库", "库存", "条码", "备注"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                sheet.setColumnWidth(i, 4000);
            }

            // 填充数据
            int rowNum = 1;
            for (Goods goods : goodsList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(goods.getId());
                row.createCell(1).setCellValue(goods.getName());
                row.createCell(2).setCellValue(storageIdToName.getOrDefault(goods.getStorage(), ""));
                row.createCell(3).setCellValue(goods.getType() != null ? goods.getType() : 0);
                row.createCell(4).setCellValue(goods.getBarcode() != null ? goods.getBarcode() : "");
                row.createCell(5).setCellValue(goods.getRemark() != null ? goods.getRemark() : "");
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("商品列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/template/goods")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("商品导入模板");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"商品名称*", "仓库名称*", "库存数量*", "商品条码", "备注"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                sheet.setColumnWidth(i, 5000);
            }

            // 创建示例行
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue("示例商品");
            sampleRow.createCell(1).setCellValue("北京朝阳主仓");
            sampleRow.createCell(2).setCellValue(100);
            sampleRow.createCell(3).setCellValue("123456789");
            sampleRow.createCell(4).setCellValue("示例备注");

            // 创建说明行
            Row noteRow = sheet.createRow(3);
            noteRow.createCell(0).setCellValue("说明: * 为必填项，仓库名称必须在系统中已存在");

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("商品导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取单元格值
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                // 判断是否为整数
                double numValue = cell.getNumericCellValue();
                if (numValue == Math.floor(numValue)) {
                    return String.valueOf((int) numValue);
                }
                return String.valueOf(numValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return null;
        }
    }
}
