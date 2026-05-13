package com.warehouse;

import io.github.cdimascio.dotenv.Dotenv;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.warehouse.mapper")
public class WarehouseApplication {

    public static void main(String[] args) {
        // 加载 .env 文件
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            dotenv.entries().forEach(e ->
                System.setProperty(e.getKey(), e.getValue())
            );
            System.out.println("[INFO] .env file loaded successfully");
        } catch (Exception e) {
            System.out.println("[WARN] .env file not found, using system environment variables");
        }

        SpringApplication.run(WarehouseApplication.class, args);
    }

}
