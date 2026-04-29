# 智能个人日程管理系统 - 项目启动指南

## 📋 项目概述

SmartSchedule 是一款功能完整的智能个人日程管理系统，采用前后端分离架构，支持自然语言输入、智能冲突检测和多视图展示。

## 🏗️ 技术栈

### 前端
- Vue 3.4 + TypeScript
- Element Plus 2.6+
- Vite 5
- ECharts 5.5+
- Pinia 状态管理

### 后端
- Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Redis 7.x

## 🚀 快速启动

### 方式一：Docker + 本地混合部署（推荐）

使用 Docker 管理基础设施服务（MySQL、Redis），后端和前端在本地分别启动。

```bash
# 1. 确保已安装 Docker 和 Docker Compose
docker --version
docker-compose --version

# 2. 进入项目根目录
cd E:\CodeProject\Graduation_Project2026

# 3. 启动基础设施 (MySQL + Redis)
docker-compose up -d

# 4. 确认 MySQL 和 Redis 已启动
docker-compose ps
```

#### 4. 后端启动

```bash
# 进入后端目录
cd backend

# 修改配置文件 (可选)
# 编辑 src/main/resources/application.yml 中的数据库连接信息

# 启动后端
mvn spring-boot:run

# 或打包后运行（前提是已构建过）
mvn clean package -DskipTests
java -jar target/smart-schedule-backend-1.0.0.jar
```

后端将在 http://localhost:8080 启动

#### 5. 前端启动

```bash
# 新开一个终端
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端将在 http://localhost:3000 启动

**注意：** 三个部分（MySQL+Redis、后端、前端）全部启动后，才能正常使用系统。

### 方式二：全本地开发环境

#### 前置条件
- JDK 17+
- Node.js 18+
- Maven 3.8+
- MySQL 8.0+（已安装并启动）
- Redis 7.x（已安装并启动）

#### 1. 启动 MySQL 和 Redis

```bash
# 启动 MySQL（Windows 服务方式）
net start MySQL80

# 启动 Redis（默认端口 6379）
redis-server
```

或使用 Docker 启动基础设施（如果已安装 Docker）：

```bash
docker-compose up -d mysql redis
```

#### 2. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE smart_schedule DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行初始化脚本（建表）和测试数据
USE smart_schedule;
SOURCE backend/sql/init.sql;
SOURCE backend/sql/test_data.sql;
```

#### 3. 后端启动

```bash
# 进入后端目录
cd backend

# 编辑 src/main/resources/application.yml 中的数据库连接信息（如果密码不同）

# 启动后端
mvn spring-boot:run

# 或打包后运行
mvn clean package -DskipTests
java -jar target/smart-schedule-backend-1.0.0.jar
```

后端将在 http://localhost:8080 启动

#### 4. 前端启动

```bash
# 新开一个终端
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端将在 http://localhost:3000 启动

## 🔧 配置说明

### 后端配置

编辑 `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_schedule
    username: root
    password: your_password
  
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: your_jwt_secret_key
  expiration: 86400000  # 24小时
```

### 前端配置

前端已配置好API代理，会自动将 `/api` 请求转发到后端。如需修改，编辑 `frontend/vite.config.ts`:

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 🧪 测试账号

> **注意：** 测试账号需要执行 `test_data.sql` 导入后才能使用。
> 使用 Docker 方式启动 MySQL 时会自动导入；本地开发方式需手动执行 `SOURCE backend/sql/test_data.sql;`

```
用户名: testuser
邮箱: test@example.com
密码: 123456
```

推荐通过前端注册新账号进行测试。

## 📚 API文档

后端API基础URL: `http://localhost:8080/api`

### 认证接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /auth/login | 用户登录 |
| POST | /auth/register | 用户注册 |
| GET | /auth/userinfo | 获取用户信息 |
| PUT | /auth/profile | 更新用户资料 |
| POST | /auth/logout | 用户退出 |

### 日程接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /schedules | 获取日程列表 |
| GET | /schedules/{id} | 获取单个日程 |
| POST | /schedules | 创建日程 |
| PUT | /schedules/{id} | 更新日程 |
| DELETE | /schedules/{id} | 删除日程 |
| GET | /schedules/conflicts | 检查冲突 |
| POST | /schedules/parse-nlp | NLP解析 |

### 标签接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /tags | 获取标签列表 |
| POST | /tags | 创建标签 |
| PUT | /tags/{id} | 更新标签 |
| DELETE | /tags/{id} | 删除标签 |

## 🐳 Docker命令

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down

# 重新构建并启动
docker-compose up -d --build

# 进入容器（调试用）
docker exec -it smart-schedule-mysql /bin/bash
docker exec -it smart-schedule-redis /bin/sh
```

## 🛠️ 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 验证数据库用户名和密码
- 确保数据库已创建

### 2. 前端无法访问后端API
- 检查后端服务是否正常运行
- 验证API代理配置
- 检查CORS配置

### 3. Docker容器启动失败
```bash
# 清理Docker环境
docker-compose down -v
docker system prune -f

# 重新启动
docker-compose up -d
```

### 4. 端口占用
```bash
# Windows查看端口占用
netstat -ano | findstr :8080
netstat -ano | findstr :3000
netstat -ano | findstr :3306
netstat -ano | findstr :6379

# 结束占用进程
taskkill /PID <进程ID> /F
```

## 📦 项目结构

```
Graduation_Project2026/
├── frontend/                 # 前端项目
│   ├── src/
│   │   ├── api/             # API接口
│   │   ├── components/      # Vue组件
│   │   ├── pages/           # 页面
│   │   ├── stores/          # 状态管理
│   │   └── styles/          # 样式
│   ├── package.json
│   ├── vite.config.ts
│   └── Dockerfile
│
├── backend/                  # 后端项目
│   ├── src/main/java/com/smartschedule/
│   │   ├── controller/      # 控制器
│   │   ├── service/         # 服务层
│   │   ├── repository/      # 数据访问
│   │   ├── entity/          # 实体类
│   │   ├── dto/             # 数据传输对象
│   │   ├── security/        # 安全配置
│   │   └── config/          # 配置类
│   ├── src/main/resources/
│   │   └── application.yml  # 应用配置
│   ├── sql/
│   │   └── init.sql         # 数据库初始化
│   ├── pom.xml
│   └── Dockerfile
│
├── docker-compose.yml        # Docker编排
└── README.md                 # 项目说明
```

## 🎯 功能特性

✅ 用户认证（登录/注册/JWT）  
✅ 日历视图（日/周/月）  
✅ 日程管理（CRUD）  
✅ NLP智能输入  
✅ 冲突检测  
✅ 标签管理  
✅ 统计报表  
✅ 提醒设置  
✅ 用户配置  

## 📞 技术支持

如有问题，请检查：
1. 所有服务是否正常启动
2. 数据库连接是否正常
3. 端口是否被占用
4. Docker是否正常运行

## 🔗 相关链接

- Vue.js: https://vuejs.org/
- Spring Boot: https://spring.io/projects/spring-boot/
- Element Plus: https://element-plus.org/
- MySQL: https://www.mysql.com/

---

**版本**: 1.0.0  
**最后更新**: 2024-01-15  
**许可证**: MIT
