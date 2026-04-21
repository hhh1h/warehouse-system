# 仓库管理系统

基于 SpringBoot + Vue 前后端分离的仓库管理系统

## 技术栈

- **后端**：SpringBoot 2.7、MyBatis Plus 3.5.5、MySQL 8.0、Apache POI 5.2.3
- **前端**：Vue 2.6.14、Element UI 2.15.13、Axios、ECharts 5.4.2

## 功能模块

| 模块 | 功能 |
|------|------|
| 商品管理 | 商品增删改查、批量删除、条码管理、二维码生成、入库/出库操作 |
| 仓库管理 | 仓库增删改查 |
| 记录管理 | 出入库记录查看、筛选、删除 |
| 统计报表 | 库存数据统计图表 |
| 库存预警 | 低库存自动预警、手动检查、标记已读 |
| 用户管理 | 用户登录、权限管理 |
| Excel功能 | 下载模板、批量导入、导出Excel |

## 环境要求

- JDK 1.8+
- Maven 3.6+
- Node.js 14+
- MySQL 8.0+

## 快速开始

### 1. 初始化数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行初始化脚本
source sql/init.sql
```

或直接在命令行执行：
```bash
mysql -u root -p < sql/init.sql
```

### 2. 修改数据库配置（可选）

编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/warehouse
    username: root
    password: your_password  # 修改为你的密码
```

### 3. 启动后端

```bash
cd backend
mvn clean package -DskipTests
java -jar target/warehouse-0.0.1-SNAPSHOT.jar
```

后端启动成功后会显示：
```
Started WarehouseApplication in X seconds
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run serve
```

前端启动成功后会显示：
```
Local: http://localhost:8080/
```

### 5. 访问系统

打开浏览器访问：**http://localhost:8080**

默认账号：
- 用户名：`admin`
- 密码：`123456`

## 项目结构

```
warehouse-system/
├── backend/                    # 后端项目（Spring Boot）
│   ├── src/main/java/
│   │   └── com/warehouse/
│   │       ├── controller/     # 控制器层
│   │       ├── entity/        # 实体类
│   │       ├── mapper/        # 数据访问层
│   │       ├── common/        # 公共类
│   │       └── config/        # 配置类
│   └── src/main/resources/
│       ├── application.yml    # 应用配置
│       └── mapper/            # MyBatis XML
├── frontend/                   # 前端项目（Vue.js）
│   ├── src/
│   │   ├── views/            # 页面组件
│   │   ├── router/          # 路由配置
│   │   └── utils/           # 工具类
│   └── package.json
├── sql/                       # 数据库脚本
│   └── init.sql             # 完整初始化脚本
├── README.md                  # 项目说明
└── CLAUDE.md                  # AI开发指南
```

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/user/login` | POST | 用户登录 |
| `/goods` | GET | 获取商品列表 |
| `/goods` | POST | 添加商品 |
| `/goods` | PUT | 更新商品 |
| `/goods/inbound` | POST | 商品入库 |
| `/goods/outbound` | POST | 商品出库 |
| `/goods/barcode/{barcode}` | GET | 条码查询商品 |
| `/storage` | GET | 获取仓库列表 |
| `/record` | GET | 获取出入库记录 |
| `/alert` | GET | 获取预警列表 |
| `/alert/check` | POST | 检查库存预警 |
| `/excel/export/goods` | GET | 导出商品Excel |
| `/excel/template/goods` | GET | 下载导入模板 |
| `/excel/import/goods` | POST | 批量导入商品 |

## 生产环境部署

### 前端打包

```bash
cd frontend
npm run build
```

构建产物在 `frontend/dist/`，可部署到 Nginx 等静态服务器。

### Docker 部署（可选）

```bash
# 后端 Docker
docker build -t warehouse-backend ./backend
docker run -d -p 9090:9090 warehouse-backend
```

## 常见问题

### 1. 前端无法连接后端
检查 `frontend/vue.config.js` 的 proxy 配置是否正确指向 `http://localhost:9090`

### 2. 数据库连接失败
确认 MySQL 服务已启动，密码正确，数据库已创建

### 3. Excel 功能无法使用
确保已执行 `mvn clean package` 重新编译，Apache POI 依赖已加载

### 4. 库存预警不触发
确保商品库存 < 10，点击"检查库存"按钮或进行出入库操作

## 许可证

MIT License
