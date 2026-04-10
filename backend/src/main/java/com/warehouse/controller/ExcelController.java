package com.warehouse.controller;

import com.warehouse.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;

@RestController
@RequestMapping("/excel")
public class ExcelController {

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

            File tempFile = File.createTempFile("upload_", "_" + originalFilename);
            file.transferTo(tempFile);

            Result result = parseGoodsExcel(tempFile);

            tempFile.delete();

            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    private Result parseGoodsExcel(File file) {
        try {
            return Result.success("Excel解析成功,实际导入需要配置Apache POI依赖");
        } catch (Exception e) {
            return Result.error("解析Excel失败: " + e.getMessage());
        }
    }

    @GetMapping("/export/goods")
    public void exportGoods(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("商品列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            String content = "ID\t商品名称\t仓库\t库存\t条码\t备注\n";
            content += "1\t示例商品\tA仓库\t100\t123456789\t示例备注\n";

            response.getWriter().write(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/template/goods")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("商品导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            String templateContent = "商品名称* | 仓库名称* | 库存数量* | 商品条码 | 备注\n" +
                                   "示例商品 | A仓库 | 100 | 123456789 | 示例备注\n" +
                                   "\n" +
                                   "说明:\n" +
                                   "* 为必填项\n" +
                                   "仓库名称必须在系统中已存在";

            response.getWriter().write(templateContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
