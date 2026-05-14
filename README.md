# 仓库管理系统

基于 Spring Boot + Vue 的前后端分离仓库管理系统。

## 技术栈

- **后端**: Spring Boot 2.7, MyBatis Plus, MySQL 8.0
- **前端**: Vue 2.6, Element UI, Axios, ECharts
- **其他**: Apache POI (Excel), Tauri (桌面端), DeepSeek API (AI助手)

## 功能

- 商品管理（增删改查、条码、二维码、入库/出库）
- 仓库管理（增删改查）
- 出入库记录（查看、筛选）
- 库存统计图表
- 库存预警（低库存检测）
- 用户管理（登录、权限）
- Excel 导入/导出
- AI 智能助手（悬浮按钮 → 对话框）

## 快速开始

### 1. 初始化数据库

```bash
mysql -u root -p < sql/init.sql
```

### 2. 启动后端

```bash
cd backend
mvn clean package -DskipTests
java -jar target/warehouse-0.0.1-SNAPSHOT.jar
```

后端默认运行在 `http://localhost:9090`。

### 3. 启动前端

```bash
cd frontend
npm install
npm run serve
```

前端默认运行在 `http://localhost:8080`。

### 4. 登录

打开浏览器访问 `http://localhost:8080`，默认账号 `admin / 123456`。

## 配置说明

编辑 `backend/src/main/resources/application.yml`：

- **数据库**: 修改 `spring.datasource` 下的 url / username / password
- **AI助手**: 修改 `ai.api-key` 为你的 DeepSeek API Key

## 项目结构

```
warehouse-system/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 前端
├── sql/              # 数据库初始化脚本
├── docker-compose.yml
└── README.md
```

## 部署

```bash
# 前端打包
cd frontend && npm run build    # 产物在 frontend/dist/

# Docker 部署
docker compose up -d
```
