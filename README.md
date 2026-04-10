# 仓库管理系统

## 项目简介
基于 SpringBoot + Vue 前后端分离的仓库管理系统

## 技术栈
- 后端：SpringBoot 2.7、MyBatis、MySQL 8.0
- 前端：Vue2、ElementUI、Axios

## 功能模块
- 商品管理：商品的增删改查、批量删除
- 仓库管理：仓库信息管理
- 记录管理：出入库记录
- 统计报表：数据统计图表
- 用户管理：用户权限管理

## 快速开始

### 后端启动
```bash
cd backend
mvn clean package
java -jar target/warehouse-0.0.1-SNAPSHOT.jar
```

### 前端启动
```bash
cd frontend
npm install
npm run serve
```

## 默认账号
- 用户名：admin
- 密码：123456
