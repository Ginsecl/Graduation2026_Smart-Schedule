# 智能个人日程管理系统（SmartSchedule）后端服务

## 项目简介

智能个人日程管理系统（SmartSchedule）后端服务是一个基于 Spring Boot 3.2 开发的 RESTful API 后端系统，旨在为个人用户提供全面的日程管理解决方案。系统整合了自然语言处理（NLP）技术、智能提醒机制以及可视化统计分析功能，帮助用户高效管理日常事务，合理规划时间安排。

本项目采用分层架构设计，基于 Spring Security 实现安全的用户认证机制，通过 JWT 技术提供无状态的身份验证服务。系统支持 MySQL 8.0 数据库进行持久化存储，利用 Redis 实现缓存和会话管理，并采用 Quartz 调度框架处理定时提醒任务。NLP 模块能够解析用户输入的自然语言描述，自动提取日程关键信息并创建日程条目，大幅提升用户体验。

项目主要特性包括：支持多种日程类型（会议、任务、截止日期、个人事务、生日、旅行等）的管理；提供灵活的标签分类系统；实现日程时间冲突检测与智能解决方案推荐；支持多维度的使用统计分析；以及基于 WebSocket 的实时提醒通知功能。

## 技术架构

### 系统架构概述

本系统采用经典的分层架构模式，自下而上分为数据访问层（Repository）、业务逻辑层（Service）、控制层（Controller）和接口层（API）。各层之间遵循依赖倒置原则，高层模块不依赖低层模块的具体实现，而是依赖抽象接口。这种设计提高了系统的可维护性、可扩展性和可测试性。

数据访问层基于 Spring Data JPA 和 MyBatis-Plus 双框架实现。JPA 用于处理实体与数据库之间的对象关系映射，简化了 CRUD 操作的开发；MyBatis-Plus 则用于复杂查询场景，提供了强大的动态 SQL 能力和条件构造器。业务逻辑层封装了核心业务规则，包括日程创建与更新、冲突检测算法、NLP 语义解析、提醒策略引擎等。控制层负责接收 HTTP 请求、参数验证、调用服务层并将结果封装为统一的响应格式返回给客户端。

安全模块采用 Spring Security 框架进行认证与授权管理。用户登录时通过自定义的 CustomUserDetailsService 加载用户信息，验证通过后由 JwtTokenProvider 生成包含用户身份信息的令牌。后续请求通过 JwtAuthenticationFilter 拦截验证请求头中的令牌有效性。Redis 用于存储令牌黑名单，支持用户主动登出功能。

定时任务模块基于 Quartz 调度框架实现。ReminderQuartzConfig 配置类负责管理 JobDetail 和 Trigger 的创建与调度，ReminderSendJob 执行具体的提醒发送逻辑。系统支持多种提醒策略，包括日程开始前提醒、重复提醒等，并通过 WebSocket 向在线用户推送实时通知。

### 技术栈清单

后端核心技术栈包括：Spring Boot 3.2.2 作为基础框架，提供了自动配置、起步依赖等开发便利；Spring Data JPA 2.4.5 处理对象关系映射；MyBatis-Plus 3.5.5 提供增强的数据库操作能力；Spring Security 6.2 实现安全控制；Spring Data Redis 3.2 管理缓存和会话；Spring Quartz 2.3 处理定时任务；Spring WebSocket 6.1 支持实时通信。

数据库层采用 MySQL 8.0 作为主数据库，使用 HikariCP 连接池管理数据库连接。缓存层使用 Redis 7.0，存储会话数据、令牌黑名单和热点查询结果。

认证授权方面使用 JWT（JSON Web Token）技术，具体使用 jjwt 0.11.5 版本实现令牌的生成与验证。

工具类库包括：Hutool 5.8.23 提供丰富的工具方法集，简化日期处理、字符串操作、JSON 转换等常见任务；Lombok 1.18.30 通过注解自动生成Getter/Setter、构造函数等样板代码。

外部服务集成方面，系统集成了 OpenAI Java Client 0.12.2，用于调用大语言模型 API 实现高级 NLP 功能。

### 依赖版本信息

项目基于 Maven 进行依赖管理，主要依赖版本如下：Java 版本要求 17 以上；Spring Boot 版本 3.2.2；MyBatis-Plus 版本 3.5.5；JJWT 版本 0.11.5；Hutool 版本 5.8.23；OpenAI Java Client 版本 0.12.2。

详细的依赖配置请参阅项目根目录下的 pom.xml 文件。该文件定义了所有生产依赖和测试依赖，以及相应的版本管理。

## 快速开始

### 环境要求

开发及运行本项目需要满足以下环境要求：

**Java 运行环境**：系统需要安装 JDK 17 或更高版本。建议使用 Eclipse Temurin（Adoptium）提供的 OpenJDK 发行版，该版本经过充分测试且与项目依赖兼容。可以通过在终端执行 java -version 命令验证 Java 版本。

**数据库服务**：需要 MySQL 8.0 或更高版本。MySQL 8.0 引入了许多新特性，包括窗口函数、CTE（公用表表达式）、JSON 函数增强等，这些特性在本项目的统计查询中被使用。确保 MySQL 服务已启动，并创建了相应的数据库和用户。

**缓存服务**：需要 Redis 7.0 或更高版本。Redis 用于存储用户会话信息、JWT 令牌黑名单以及热点数据缓存。建议生产环境配置 Redis 持久化，以防数据丢失。

**构建工具**：需要 Apache Maven 3.6 或更高版本。Maven 用于管理项目依赖、编译代码和打包部署。确保 Maven 已配置阿里云镜像或其他国内镜像源，以加快依赖下载速度。

### 数据库配置说明

数据库配置在 src/main/resources/application.yml 文件的 spring.datasource 节中定义。开发环境下可以按以下示例配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_schedule?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: root123456
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

配置参数说明如下：url 字段指定数据库连接地址，smart_schedule 为数据库名称，useUnicode 和 characterEncoding 参数确保中文正确存储，serverTimezone 设置为中国时区；username 和 password 为数据库访问凭据；driver-class-name 指定使用 MySQL 8.x 驱动；HikariCP 连接池参数中，maximum-pool-size 设置最大连接数为 20，minimum-idle 设置最小空闲连接数为 5，connection-timeout 设置连接超时时间为 30 秒。

首次运行时，JPA 的 ddl-auto 配置设为 update，会自动根据实体类创建或更新数据表结构。生产环境建议将此参数改为 validate 或使用数据库迁移工具（如 Flyway、Liquibase）管理表结构变更。

数据库初始化脚本位于 sql/init.sql 文件，包含完整的建表语句和示例数据。可以通过执行该脚本快速初始化数据库环境。

### Redis配置说明

Redis 配置同样在 application.yml 文件的 spring.data.redis 节中定义：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0
      timeout: 5000
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
```

配置参数说明：host 指定 Redis 服务器地址，port 指定端口号，password 为访问密码（空表示无密码），database 选择使用的数据库编号（0-15），timeout 设置连接超时时间，Lettuce 连接池参数设置最大连接数为 8。

确保 Redis 服务已启动并正常运行。如果 Redis 与应用部署在不同服务器，需要相应修改 host 参数。生产环境建议设置密码认证，并配置 Redis 持久化策略。

### JWT密钥配置

JWT 密钥在 application.yml 的 jwt 节中定义：

```yaml
jwt:
  secret: SmartScheduleSecretKey2024ForJWTTokenGeneration
  expiration: 86400000
```

secret 参数为用于签名 JWT 令牌的密钥，生产环境务必修改为足够复杂的随机字符串，建议长度不少于 32 个字符，并妥善保管不要泄露。expiration 参数指定令牌有效期，单位为毫秒，当前配置为 86400000（24 小时）。

系统启动时会读取这些配置并创建 JwtConfigProperties 配置类实例。JwtTokenProvider 依赖该配置类完成令牌的创建、验证和解析操作。

### 运行项目步骤

开发环境运行项目有两种常用方式：

**方式一：使用 Maven 命令行**

```bash
cd e:\BaiduSyncdisk\Code\Graduation_Project2026\backend
mvn clean install
mvn spring-boot:run
```

mvn clean install 命令会清理 target 目录、编译源代码、运行测试并打包项目。首次运行会下载所有依赖，可能需要几分钟时间。mvn spring-boot:run 命令直接启动 Spring Boot 应用。

**方式二：使用 IDE 运行**

在 IntelliJ IDEA 或 Eclipse 中打开项目，等待 Maven 索引建立完成。然后找到主类 SmartScheduleApplication，右键选择 Run As → Spring Boot App 即可启动应用。

应用启动成功后，默认监听 8080 端口。可以通过访问 http://localhost:8080/api/v1/schedules/nlp/test 验证服务是否正常运行，正常情况下返回 {"code":200,"message":"success","data":"NLP endpoint is working"}。

## 项目结构

### 包结构说明

项目源代码组织在 src/main/java/com/smartschedule 目录下，采用按功能模块分包的结构：

**controller 包**：存放所有 REST 控制器，负责处理 HTTP 请求和响应。AuthController 处理用户认证相关接口（登录、注册、退出）；ScheduleController 处理日程的增删改查操作；ReminderController 处理提醒管理；TagController 处理标签管理；StatisticsController 处理统计分析；ConflictController 处理冲突检测；UserController 处理用户信息管理。

**service 包及其 impl 子包**：存放业务逻辑接口和实现类。Service 层是系统的核心，封装了所有业务规则和处理逻辑。AuthService 实现用户认证和授权逻辑；ScheduleService 实现日程管理业务逻辑；NlpParserService 处理自然语言解析；ReminderService 处理提醒的创建、更新、触发等；StatisticsService 计算和返回各类统计数据；TagService 管理用户标签；ConflictDetectionService 检测时间冲突并提供解决方案。

**repository 包**：存放数据访问接口。Spring Data JPA 的 Repository 接口提供了强大的数据访问能力，支持方法命名约定查询、@Query 自定义查询等。ScheduleRepository 提供日程数据的 CRUD 操作和复杂条件查询；UserRepository 提供用户数据的访问；ReminderRepository 提供提醒数据的查询。

**entity 包**：存放 JPA 实体类，与数据库表一一对应。User 实体映射用户信息表；Schedule 实体映射日程表；Reminder 实体映射提醒表；Tag 实体映射标签表。实体类使用 JPA 注解定义字段与列的映射关系，以及实体之间的关联关系。

**dto 包及其子包**：存放数据传输对象。DTO 用于在 Controller 与 Service 层之间传递数据，隔离实体与外部接口，提高系统安全性。CreateScheduleRequest、UpdateScheduleRequest 等请求 DTO 接收客户端请求参数；ScheduleDTO、UserDTO 等响应 DTO 封装返回给客户端的数据；statistics 子包存放统计相关的 DTO；conflict 子包存放冲突检测相关的 DTO。

**config 包及其子包**：存放配置类。SecurityConfig 配置 Spring Security 安全策略；RedisConfig 配置 Redis 连接和序列化方式；JwtConfigProperties 封装 JWT 配置参数；CorsConfig 配置跨域资源共享策略；AsyncConfig 配置异步任务执行器；websocket 子包存放 WebSocket 相关配置。

**security 包**：存放安全认证相关类。JwtTokenProvider 负责 JWT 令牌的创建、验证和解析；JwtAuthenticationFilter 拦截请求并验证令牌；CustomUserDetailsService 加载用户详情用于认证。

**nlp 包及其子包**：存放 NLP 相关的代码。NLPParserService 是 NLP 解析服务接口；NlpScheduleService 处理基于自然语言创建日程的业务；EntityExtractor 提取文本中的实体信息；TimeExpressionExtractor 提取时间表达式；controller 子包存放 NLP 专用控制器。

**scheduler 包**：存放 Quartz 定时任务相关代码。ReminderQuartzConfig 配置提醒任务的调度；ReminderSendJob 实现提醒发送的 Job 类。

**common 包**：存放公共工具类和常量。ApiResponse 封装统一的响应格式；Constants 定义系统常量；DateUtils 提供日期处理工具方法。

**exception 包**：存放异常类和全局异常处理器。BusinessException、ResourceNotFoundException、UnauthorizedException 是自定义业务异常；GlobalExceptionHandler 统一处理所有未捕获的异常，返回格式化的错误信息。

### 各层职责说明

**控制层（Controller）职责**：接收并解析 HTTP 请求参数；进行参数校验和格式验证；调用服务层处理业务逻辑；将服务层返回的结果封装为统一的 API 响应格式；处理异常情况并返回适当的 HTTP 状态码。控制器应保持简洁，不包含复杂业务逻辑。

**服务层（Service）职责**：实现核心业务逻辑和规则；协调多个数据访问操作保证事务一致性；处理业务层面的异常，转换为用户友好的错误信息；提供事务管理边界。服务类是系统的业务核心，应具备良好的可测试性。

**数据访问层（Repository）职责**：封装数据访问逻辑，提供 CRUD 操作；定义复杂查询方法；使用 Spring Data JPA 的派生查询和方法命名约定简化开发。数据访问层应只关注数据存储，不包含业务逻辑。

**实体层（Entity）职责**：映射数据库表结构；定义表之间的关联关系；使用 JPA 注解配置列属性（长度、可空、唯一性等）。实体类应保持简单，只有属性和必要的关联映射。

**DTO层职责**：定义接口的数据传输格式；在不同层级之间传递数据；隔离内部实体与外部接口，增强系统安全性。DTO 应根据接口需求设计，可以与实体类结构不同。

**配置层职责**：配置第三方服务的连接参数；配置框架行为参数；定义 Bean 之间的依赖关系。配置类应集中管理，便于维护。

## API接口文档

### 认证接口（/api/v1/auth 或 /auth）

认证接口提供用户注册、登录、登出和基本信息获取功能，采用 RESTful 设计风格。

**用户登录接口**

请求方式为 POST，路径为 /auth/login 或 /api/v1/auth/login，请求体为 JSON 格式：

```json
{
  "username": "testuser",
  "password": "password123"
}
```

成功响应返回 200 状态码和数据：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "nickname": "测试用户",
      "avatar": null,
      "timezone": "Asia/Shanghai"
    }
  }
}
```

失败响应返回 400 状态码，message 字段说明错误原因。

**用户注册接口**

请求方式为 POST，路径为 /auth/register，请求体为 JSON 格式：

```json
{
  "username": "newuser",
  "email": "new@example.com",
  "password": "password123",
  "nickname": "新用户"
}
```

所有字段均为必填，username 和 email 会进行唯一性校验。注册成功后自动登录并返回令牌。

**获取用户信息接口**

请求方式为 GET，路径为 /auth/userinfo，需要在请求头中携带 Authorization 令牌。成功返回当前登录用户的基本信息。

**更新用户资料接口**

请求方式为 PUT，路径为 /auth/profile，请求体包含需要更新的字段。支持更新 nickname、avatar、timezone 等字段。

**退出登录接口**

请求方式为 POST，路径为 /auth/logout，调用后当前令牌会被加入黑名单，无法再使用。

### 日程管理接口（/api/v1/schedules）

日程管理是系统的核心功能模块，提供完整的日程 CRUD 操作。

**查询日程列表**

请求方式为 GET，路径为 /api/v1/schedules，查询参数 startDate 和 endDate 指定日期范围（格式：yyyy-MM-dd）。返回指定时间段内的所有日程，按开始时间排序。

**获取单个日程**

请求方式为 GET，路径为 /api/v1/schedules/{id}，路径参数 id 为日程 ID。返回日程的详细信息，包括标题、描述、时间、类型、状态、重要性、地点、参与者等。

**创建日程**

请求方式为 POST，路径为 /api/v1/schedules，请求体示例：

```json
{
  "title": "团队周会",
  "description": "讨论本周工作进度和下周计划",
  "startTime": "2024-01-15 10:00:00",
  "endTime": "2024-01-15 11:00:00",
  "type": "MEETING",
  "importance": 3,
  "location": "会议室A",
  "participants": ["张三", "李四", "王五"]
}
```

type 可选值：MEETING（会议）、TASK（任务）、DEADLINE（截止日期）、PERSONAL（个人）、BIRTHDAY（生日）、TRAVEL（旅行）、OTHER（其他）。importance 取值 1-5，数字越大越重要。

**更新日程**

请求方式为 PUT，路径为 /api/v1/schedules/{id}，请求体同创建日程，只包含需要更新的字段。更新时会验证日程归属，非所有者无法修改。

**删除日程**

请求方式为 DELETE，路径为 /api/v1/schedules/{id}。删除日程会同时删除关联的提醒记录，无法恢复。

**查询冲突日程**

请求方式为 GET，路径为 /api/v1/schedules/conflicts，查询参数指定时间段和可选的排除日程 ID。返回该时间段内与已有日程存在时间重叠的所有日程。

### NLP自然语言解析接口（/api/v1/schedules/nlp）

NLP 模块提供自然语言处理能力，将用户的自然语言描述转换为结构化的日程信息。

**解析自然语言文本**

请求方式为 POST，路径为 /api/v1/schedules/nlp，请求体：

```json
{
  "text": "明天下午三点和李四开会讨论项目进度"
}
```

解析结果示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "schedule": {
      "title": "和李四开会讨论项目进度",
      "startTime": "2024-01-16 15:00:00",
      "endTime": "2024-01-16 16:00:00",
      "type": "MEETING",
      "participants": ["李四"]
    },
    "confidence": 0.95,
    "extractedEntities": {
      "participants": ["李四"],
      "time": "明天下午三点"
    }
  }
}
```

支持的表达示例：

- "明天上午九点开会" → 自动识别日期、时间和日程类型
- "下周三下午两点到四点做项目汇报" → 识别时间范围
- "本周五下午三点提醒我给客户打电话" → 创建带提醒的日程
- "明天晚上八点有瑜伽课" → 识别活动类型和具体时间

**解析并创建日程**

请求方式为 POST，路径为 /api/v1/schedules/nlp/create，先解析文本获取日程信息，验证解析结果完整后直接创建日程。如果解析结果不完整（如缺少必要的时间信息），返回错误提示。

**冲突检测（基于解析结果）**

请求方式为 POST，路径为 /api/v1/schedules/nlp/check-conflict，传入解析结果进行冲突检测，返回冲突信息和建议的解决方案。

### 提醒管理接口（/api/v1/reminders）

提醒模块管理日程的提醒设置，支持多种提醒策略。

**获取提醒列表**

请求方式为 GET，路径为 /api/v1/reminders，支持分页查询和按日程筛选。返回提醒列表，包含提醒时间、类型、状态等信息。

**获取单个提醒**

请求方式为 GET，路径为 /api/v1/reminders/{id}，返回提醒的详细信息。

**创建提醒**

请求方式为 POST，路径为 /api/v1/reminders，请求体示例：

```json
{
  "scheduleId": 1,
  "type": "BEFORE",
  "minutes": 30,
  "enabled": true
}
```

type 可选值：BEFORE（日程开始前）、EXACT（精确时刻）、REPEAT（重复提醒）。minutes 指定提前时间（分钟），仅对 BEFORE 类型有效。

**更新提醒**

请求方式为 PUT，路径为 /api/v1/reminders/{id}，可更新提醒的时间、类型、启用状态等。

**删除提醒**

请求方式为 DELETE，路径为 /api/v1/reminders/{id}。

**确认提醒**

请求方式为 POST，路径为 /api/v1/reminders/{id}/acknowledge，用户确认收到提醒后调用，记录确认状态。

**获取日程的所有提醒**

请求方式为 GET，路径为 /api/v1/reminders/schedule/{scheduleId}。

**获取提醒规则**

请求方式为 GET，路径为 /api/v1/reminders/rules，返回系统预设的提醒时间点选项。

**获取提醒设置**

请求方式为 GET，路径为 /api/v1/reminders/settings，返回用户的提醒偏好设置，包括默认提醒时间、是否启用声音提醒、是否启用桌面通知等。

**更新提醒设置**

请求方式为 PUT，路径为 /api/v1/reminders/settings，请求体包含提醒设置选项。

### 标签管理接口（/api/v1/tags）

标签系统帮助用户分类和组织日程，支持按标签筛选和查看。

**获取用户所有标签**

请求方式为 GET，路径为 /api/v1/tags，返回当前用户创建的所有标签，包含标签名称和颜色。

**获取单个标签**

请求方式为 GET，路径为 /api/v1/tags/{id}。

**创建标签**

请求方式为 POST，路径为 /api/v1/tags，请求体示例：

```json
{
  "name": "工作",
  "color": "#409EFF"
}
```

标签名称在同一用户下不能重复，color 字段指定标签显示颜色，支持十六进制颜色代码。

**更新标签**

请求方式为 PUT，路径为 /api/v1/tags/{id}，可更新标签名称和颜色。

**删除标签**

请求方式为 DELETE，路径为 /api/v1/tags/{id}，删除标签会自动解除该标签与所有日程的关联。

**获取日程的标签**

请求方式为 GET，路径为 /api/v1/tags/schedules/{scheduleId}，返回指定日程关联的所有标签。

### 统计报表接口（/api/v1/statistics）

统计模块提供多维度的日程数据分析，帮助用户了解时间使用情况。

**获取统计概览**

请求方式为 GET，路径为 /api/v1/statistics/overview，查询参数 startDate 和 endDate 指定统计时间段。返回该时间段内的关键指标，包括总日程数、完成率、平均时长、按时完成率等。

**获取时间分布**

请求方式为 GET，路径为 /api/v1/statistics/time-distribution，查询参数指定时间段和分组方式（DAY/WEEK/MONTH）。返回日程在不同时间段（按天/周/月）的分布情况。

**获取效率分析**

请求方式为 GET，路径为 /api/v1/statistics/efficiency，查询参数指定时间段。返回日程完成效率分析，包括实际用时与计划用时对比、延期日程比例、提前完成比例等。

**获取趋势报告**

请求方式为 GET，路径为 /api/v1/statistics/trend，查询参数 period 指定统计周期（WEEK/MONTH）。返回日程数量、按时完成率等指标随时间变化的趋势数据。

### 冲突检测接口（/api/v1/schedules/check-conflict）

冲突检测接口用于检测新日程与现有日程的时间冲突，并提供解决方案。

**检测时间冲突**

请求方式为 POST，路径为 /api/v1/schedules/check-conflict，请求体示例：

```json
{
  "startTime": "2024-01-15 10:00:00",
  "endTime": "2024-01-15 11:00:00",
  "excludeScheduleId": null
}
```

excludeScheduleId 用于更新日程时排除自身，避免与自己的原时间冲突。返回冲突检测结果，包括是否存在冲突、冲突的日程详情、重叠类型（完全重叠、部分重叠、包含关系等）。

**查找空闲时间段**

请求方式为 GET，路径为 /api/v1/schedules/free-slots，查询参数指定时间范围和需要的时长。系统会分析该时间范围内的已安排日程，返回所有满足条件的空闲时间段列表。

### 用户管理接口（/api/v1/user）

用户管理接口处理用户个人信息和偏好设置。

**获取当前用户信息**

请求方式为 GET，路径为 /api/v1/user，返回当前登录用户的详细信息，包括基本信息、个人偏好、日程统计摘要等。

**更新个人信息**

请求方式为 PUT，路径为 /api/v1/user/profile，请求体包含需要更新的字段。

**修改密码**

请求方式为 PUT，路径为 /api/v1/user/password，请求体包含原密码和新密码，验证原密码正确后才能修改。

## 核心功能说明

### 用户认证机制

系统采用基于 JWT（JSON Web Token）的无状态认证机制。Spring Security 框架负责认证授权流程的总体控制，自定义组件处理具体认证逻辑。

用户登录流程如下：首先用户提交用户名和密码到登录接口；AuthController 接收请求后调用 UserService 进行验证；UserService 通过 UserRepository 查询用户信息，使用 BCryptPasswordEncoder 验证密码；验证通过后调用 JwtTokenProvider 生成 JWT 令牌；令牌中包含用户 ID、用户名、过期时间等信息，使用 HS256 算法签名；最终将令牌和用户信息返回给客户端。

后续请求的认证流程如下：客户端在请求头 Authorization 字段中携带 Bearer 令牌；JwtAuthenticationFilter 拦截所有请求，提取并验证令牌；如果令牌有效，将用户信息注入 SecurityContext；Spring Security 根据配置决定是否允许访问目标资源。

用户登出时，系统将令牌加入 Redis 黑名单。后续请求验证令牌时，如果发现在黑名单中则拒绝访问。

JWT 令牌的有效期在配置文件中设定，默认 24 小时。用户可以在有效期内随时使用令牌访问资源，过期后需要重新登录。

### 日程管理功能

日程管理是系统的核心功能，支持完整的增删改查操作和丰富的查询能力。

创建日程时，系统会进行以下处理：验证标题不能为空，开始时间必须在结束时间之前；根据用户设置创建默认提醒（如未指定）；检测新日程是否与已有日程存在时间冲突，如存在则返回冲突警告；计算日程时长和重要性评分。

查询日程支持多种条件组合：按日期范围查询，返回该时间段内的所有日程；按日程状态筛选（待办、进行中、已完成、已取消、已延期）；按日程类型筛选；按重要性等级筛选；按标签筛选。查询结果支持分页和排序。

更新日程时，系统会验证操作的权限（只能修改自己的日程），重新检测时间冲突，并同步更新关联的提醒设置（如果时间发生变化）。

删除日程会级联删除关联的提醒记录和日程标签关联。如果日程存在子日程（重复日程的子项），子日程的 parentId 会设为 null，不会被删除。

### NLP自然语言解析

NLP 模块是本系统的特色功能，能够理解用户的自然语言描述并自动提取日程信息。该模块由多个子组件组成，各司其职协同工作。

**时间表达式提取器（TimeExpressionExtractor）** 负责识别文本中的时间信息。采用规则匹配和模式识别的方式，支持多种时间表达格式：绝对时间如"2024年1月15日下午三点"；相对时间如"明天上午九点"、"下周三"、"本周五下午"；模糊时间如"周一到周五"、"工作日上午"。提取结果包括具体日期、时间点或时间范围。

**实体提取器（EntityExtractor）** 负责识别日程中的其他实体信息。识别参与者姓名（通过人名模式匹配）；识别地点信息（通过地点关键词识别）；识别日程类型（通过关键词匹配如"会议"、"约会"、"上课"等）。

**意图分类器（IntentClassifier）** 判断用户文本的意图类型。创建日程意图、查询日程意图、修改日程意图、删除日程意图、提醒设置意图等。根据不同意图调用相应的处理逻辑。

**中文日期解析器（ChineseDateParser）** 专门处理中文日期表达。将"今天"、"明天"、"后天"转换为具体日期；处理"本周"、"下周"、"本月"等周期表达；处理农历节日和公历节日的识别。

解析结果封装在 ParseResult 对象中，包含提取到的标题、时间、地点、参与者、日程类型等字段，以及每个字段的置信度评分。

### 智能提醒机制

提醒系统采用 Quartz 调度框架实现准时的提醒通知，支持多种提醒策略。

**提醒策略引擎（ReminderStrategyEngine）** 负责计算提醒触发时间点。系统预设了常用提醒时间点：5分钟、10分钟、15分钟、30分钟、1小时、2小时、1天。用户也可以自定义提醒时间。

**提醒类型** 包括：BEFORE 类型，在日程开始前指定分钟数触发；EXACT 类型，在指定精确时刻触发；REPEAT 类型，按设定周期重复提醒（如每天上午9点）。

**提醒发送流程** 如下：Quartz 调度器在预设时间触发 ReminderSendJob；Job 查询提醒信息，获取关联日程详情；检查日程状态，如果已完成或已取消则跳过；调用 ReminderSenderService 发送提醒；根据用户提醒设置决定发送方式（WebSocket推送、后续扩展邮件、短信等）；更新提醒状态，记录发送时间。

**WebSocket实时通知** 通过 Spring WebSocket 实现。当用户在线时，提醒会通过 WebSocket 连接实时推送到客户端，无需轮询。WebSocketConfig 配置端点和握手参数，WebSocketHandler 处理消息的发送。

### 冲突检测功能

冲突检测是日程管理的关键辅助功能，帮助用户避免时间安排冲突。

**冲突检测算法** 基于时间区间比较原理。对于新日程的时间段 [start1, end1]，与已有日程的时间段 [start2, end2] 进行比较，判断是否存在重叠。重叠类型包括：完全重叠（两个时间段完全相同）；部分重叠（开始或结束时间在对方区间内）；包含关系（一个时间段完全包含另一个）；边界相接（结束时间等于另一个开始时间，通常不视为冲突）。

**冲突响应** 返回冲突日程的详细信息，包括冲突日程的标题、时间、类型等，并给出重叠时长占比。帮助用户判断冲突的严重程度。

**空闲时间查找** 算法遍历指定时间范围内的所有已安排日程，标记占用时间段，然后找出所有空闲区间。根据用户需要的时长参数，过滤掉时长不足的空闲区间，返回满足条件的空闲时间段列表。

### 统计报表功能

统计模块提供多维度的日程数据分析，帮助用户了解时间使用模式和效率。

**统计概览** 包含以下指标：统计周期内的日程总数；已完成日程数及其占比；按时完成的日程数及占比；平均每个日程的时长；完成率（已完成/总数）；延期率（延期完成/已完成）。

**时间分布分析** 按不同维度统计日程分布：按小时统计每天各时段的日程数量，识别高效工作时段；按星期统计每周各天的日程分布，识别规律性；按日程类型统计各类日程占比，了解时间投入方向。

**效率分析** 评估日程执行效率：实际用时与计划用时对比，计算时间偏差率；按时完成率统计；延期日程分析（延期原因、延期时长分布）；完成质量评分（综合考虑按时率、重要事项完成情况）。

**趋势分析** 展示关键指标随时间的变化：日程数量趋势，反映工作量变化；完成率趋势，反映执行效率变化；各类型日程占比趋势，反映关注点变化。

### 标签管理功能

标签系统提供灵活的日程分类能力，帮助用户组织和管理日程。

用户可以创建多个标签，每个标签有名称和颜色两个属性。标签按用户隔离，不同用户的标签互不影响。日程可以关联多个标签，标签与日程是多对多关系。

标签使用场景包括：按项目分类（如"项目A"、"项目B"）；按事务类型分类（如"工作"、"生活"、"学习"）；按优先级分类（如"紧急"、"重要"、"常规"）；按关联人员分类。

标签管理支持查看所有标签、修改标签信息、删除标签（自动解除关联）等操作。日程管理接口支持按标签筛选日程。

## 配置说明

### 应用配置文件结构

Spring Boot 应用的配置集中在 src/main/resources/application.yml 文件中。该文件采用 YAML 格式，层次清晰，易于维护。

配置文件主要分为以下几个部分：server 节定义服务器配置；spring 节定义 Spring 框架各类配置；mybatis-plus 节定义 MyBatis-Plus 配置；jwt 节定义 JWT 认证配置；logging 节定义日志配置。

### 数据库连接配置

数据库配置在 spring.datasource 节下：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_schedule
    username: root
    password: root123456
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

HikariCP 连接池参数说明：maximum-pool-size 表示最大连接数，建议设置为 CPU 核心数的 2-3 倍；minimum-idle 表示最小空闲连接数；connection-timeout 表示连接超时时间（毫秒）；idle-timeout 表示空闲连接超时时间；max-lifetime 表示连接最大生存时间。

JPA 配置在 spring.jpa 节下：

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
```

ddl-auto 可选值：none（不执行任何操作）、validate（验证表结构，不修改）、update（更新表结构，保留数据）、create（每次启动创建表，删除旧数据）、create-drop（启动时创建，关闭时删除）。开发环境建议使用 update，生产环境建议使用 validate。

### Redis配置

Redis 配置在 spring.data.redis 节下：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0
      timeout: 5000
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1
```

Lettuce 是 Spring Data Redis 默认使用的 Redis 客户端。以上配置设置了连接池参数，timeout 参数控制连接超时时间。

RedisConfig 配置类中定义了 RedisTemplate 和 StringRedisTemplate Bean，自定义了序列化方式（使用 JSON 序列化对象）。

### JWT配置

JWT 配置在独立的 jwt 节下：

```yaml
jwt:
  secret: SmartScheduleSecretKey2024ForJWTTokenGeneration
  expiration: 86400000
```

secret 必须足够复杂和保密，建议使用随机生成的 32 位以上字符串。生产环境中，不要将真实密钥提交到代码仓库，应通过环境变量或配置中心注入。

expiration 设置令牌有效期（毫秒），86400000 对应 24 小时。可以根据安全策略调整，如设置为 2 小时（7200000）提高安全性，但会降低用户体验。

### Quartz调度配置

Quartz 配置在 ReminderQuartzConfig 类中实现，使用 Spring Boot 的自动配置机制。系统默认使用内存存储 Job 信息（RAMJobStore），如需持久化可配置 JDBC 存储。

Spring Boot 对 Quartz 的自动配置在 application.yml 中通过 spring.quartz 节控制：

```yaml
spring:
  quartz:
    job-store-type: memory
    properties:
      org:
        quartz:
          scheduler:
            instanceName: SmartScheduleScheduler
          threadPool:
            threadCount: 10
```

threadCount 设置 Quartz 工作线程数，决定同时执行的提醒任务数量。

### WebSocket配置

WebSocket 配置在 websocket 子包下。WebSocketConfig 配置端点映射和握手参数：

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws")
                .setAllowedOrigins("*");
    }
}
```

WebSocketHandler 处理消息的发送和连接管理。当有提醒触发时，系统通过 WebSocket 向在线用户推送提醒通知。

## 数据库设计

### 主要表结构说明

**用户表（users）** 是系统的基础表，存储用户账号信息：

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | 主键、自增 | 用户唯一标识 |
| username | VARCHAR(50) | 唯一、非空 | 用户名，用于登录 |
| email | VARCHAR(100) | 唯一、非空 | 邮箱地址 |
| password | VARCHAR(255) | 非空 | BCrypt加密的密码 |
| nickname | VARCHAR(50) | 可空 | 昵称，显示名称 |
| avatar | VARCHAR(255) | 可空 | 头像URL |
| timezone | VARCHAR(50) | 默认值 | 时区设置 |
| enabled | BOOLEAN | 默认TRUE | 账户启用状态 |
| created_at | DATETIME | 非空 | 创建时间 |
| updated_at | DATETIME | 非空 | 更新时间 |

**日程表（schedules）** 存储所有日程记录：

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | 主键、自增 | 日程唯一标识 |
| user_id | BIGINT | 外键、非空 | 所属用户 |
| title | VARCHAR(200) | 非空 | 日程标题 |
| description | TEXT | 可空 | 日程描述 |
| start_time | DATETIME | 非空 | 开始时间 |
| end_time | DATETIME | 非空 | 结束时间 |
| type | VARCHAR(20) | 非空、默认OTHER | 日程类型 |
| status | VARCHAR(20) | 非空、默认SCHEDULED | 日程状态 |
| importance | INT | 非空、默认3 | 重要性等级1-5 |
| location | VARCHAR(200) | 可空 | 地点 |
| participants | JSON | 可空 | 参与者列表 |
| parent_id | BIGINT | 外键、可空 | 父日程ID（重复日程） |
| created_at | DATETIME | 非空 | 创建时间 |
| updated_at | DATETIME | 非空 | 更新时间 |

**提醒表（reminders）** 存储日程的提醒设置：

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | 主键、自增 | 提醒唯一标识 |
| schedule_id | BIGINT | 外键、非空 | 关联日程 |
| type | VARCHAR(20) | 非空、默认BEFORE | 提醒类型 |
| minutes | INT | 非空 | 提前分钟数 |
| enabled | BOOLEAN | 默认TRUE | 是否启用 |
| notified | BOOLEAN | 默认FALSE | 是否已发送 |
| created_at | DATETIME | 非空 | 创建时间 |

**标签表（schedule_tags）** 存储用户自定义标签：

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | 主键、自增 | 标签唯一标识 |
| user_id | BIGINT | 外键、非空 | 所属用户 |
| name | VARCHAR(50) | 非空 | 标签名称 |
| color | VARCHAR(20) | 非空、默认#409EFF | 标签颜色 |
| created_at | DATETIME | 非空 | 创建时间 |

**日程标签关联表（schedule_tag_relation）** 实现日程与标签的多对多关系：

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | 主键、自增 | 记录唯一标识 |
| schedule_id | BIGINT | 外键、非空 | 日程ID |
| tag_id | BIGINT | 外键、非空 | 标签ID |

**用户配置表（user_profiles）** 存储用户个性化设置：

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | 主键、自增 | 记录唯一标识 |
| user_id | BIGINT | 外键、唯一 | 用户ID |
| default_view | VARCHAR(20) | 默认week | 默认视图 |
| work_start | VARCHAR(10) | 默认09:00 | 工作开始时间 |
| work_end | VARCHAR(10) | 默认18:00 | 工作结束时间 |
| show_weekend | BOOLEAN | 默认TRUE | 是否显示周末 |
| week_start_day | INT | 默认1 | 一周开始于周几 |
| reminder_enabled | BOOLEAN | 默认TRUE | 是否启用提醒 |
| default_reminder_minutes | INT | 默认30 | 默认提前提醒分钟数 |
| reminder_sound | BOOLEAN | 默认TRUE | 是否启用声音提醒 |
| reminder_desktop | BOOLEAN | 默认TRUE | 是否启用桌面通知 |
| created_at | DATETIME | 非空 | 创建时间 |
| updated_at | DATETIME | 非空 | 更新时间 |

### 实体关系说明

用户与日程是一对多关系：一个用户可以拥有多个日程，但每个日程只属于一个用户。级联删除设置为 CASCADE，删除用户时会同时删除其所有日程。

日程与提醒是一对多关系：一个日程可以有多个提醒（如提前30分钟、提前1天等），删除日程时级联删除所有提醒。

日程与标签是多对多关系：通过中间表 schedule_tag_relation 实现关联。删除日程或删除标签时会自动解除关联关系。

日程支持自关联（父子关系）：parent_id 字段指向同一表的 id，用于处理重复日程场景。删除父日程时，子日程的 parent_id 设为 NULL。

用户与用户配置是一对一关系：user_profiles 表的 user_id 字段唯一约束，确保每个用户只有一条配置记录。

## 部署说明

### 开发环境运行

开发环境运行本项目的步骤如下：

首先确保本地已安装 JDK 17、Maven、MySQL 8.0 和 Redis 7.0，并且所有服务正常运行。然后按照快速开始章节的说明配置数据库连接和 Redis 连接。

执行数据库初始化脚本：

```bash
mysql -u root -p < sql/init.sql
```

这将创建数据库和表结构，并插入初始数据。

使用 Maven 构建并运行：

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

应用启动后，访问 http://localhost:8080/api/v1/schedules/nlp/test 验证服务是否正常。

在 IDE 中运行时，配置 VM options 或环境变量覆盖配置文件中的敏感信息，如数据库密码、JWT 密钥等。IDEA 中可在 Run Configuration 的 Environment Variables 中添加 SPRING_PROFILES_ACTIVE=dev。

### Docker部署

项目提供了 Dockerfile 用于容器化部署：

```dockerfile
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Docker 构建和运行步骤：

```bash
cd backend

# 构建镜像
docker build -t smartschedule-backend .

# 运行容器
docker run -d \
  --name smartschedule \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/smart_schedule \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e SPRING_DATA_REDIS_HOST=host.docker.internal \
  -e JWT_SECRET=your_secret_key \
  smartschedule-backend
```

容器化部署时需要注意：数据库和 Redis 服务需要在容器外部运行，或者使用 docker-compose 编排多容器；使用环境变量传递敏感配置，不要在镜像中硬编码密码；生产环境建议添加健康检查和日志收集配置。

### Docker Compose 完整部署

可以使用 docker-compose 编排完整的运行环境：

```yaml
version: '3.8'

services:
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/smart_schedule
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=root123456
      - SPRING_DATA_REDIS_HOST=redis
      - JWT_SECRET=SmartScheduleSecretKey2024ForJWTTokenGeneration
    depends_on:
      - mysql
      - redis

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=root123456
      - MYSQL_DATABASE=smart_schedule
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql

  redis:
    image: redis:7.0-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

volumes:
  mysql_data:
  redis_data:
```

执行 docker-compose up -d 启动所有服务。

### 环境变量配置

生产环境推荐使用环境变量覆盖配置文件，以下是常用配置项：

| 配置项 | 环境变量名 | 说明 |
|--------|-----------|------|
| 数据库URL | SPRING_DATASOURCE_URL | JDBC连接字符串 |
| 数据库用户名 | SPRING_DATASOURCE_USERNAME | 数据库访问用户名 |
| 数据库密码 | SPRING_DATASOURCE_PASSWORD | 数据库访问密码 |
| Redis主机 | SPRING_DATA_REDIS_HOST | Redis服务器地址 |
| Redis密码 | SPRING_DATA_REDIS_PASSWORD | Redis访问密码 |
| JWT密钥 | JWT_SECRET | JWT签名密钥 |
| 服务端口 | SERVER_PORT | 应用监听端口 |

## 开发指南

### 代码规范

本项目遵循以下代码规范：

**命名规范**：类名使用 UpperCamelCase（首字母大写的驼峰命名），如 ScheduleController；方法名、变量名使用 lowerCamelCase（首字母小写的驼峰命名），如 getScheduleById；常量名使用全大写下划线分隔，如 MAX_RETRY_COUNT；包名全部小写，如 com.smartschedule.controller。

**类结构组织**：按以下顺序排列类成员：静态常量 → 实例变量（字段）→ 构造函数 → 公共方法 → 私有方法；使用 Lombok 注解减少样板代码；实体类添加 @Entity 和 @Table 注解；DTO 类实现序列化接口。

**Controller 规范**：每个 Controller 处理一个资源模块；使用 @RestController 注解；使用类级别 @RequestMapping 指定路径前缀；方法级别注解使用具体 HTTP 方法（@GetMapping、@PostMapping 等）；使用 @Valid 注解启用参数校验。

**Service 规范**：Service 接口定义业务方法签名；ServiceImpl 实现类添加 @Service 注解；使用构造函数注入依赖（通过 Lombok 的 @RequiredArgsConstructor）；业务逻辑应保持原子性，复杂业务拆分为多个方法。

**异常处理**：使用自定义业务异常；业务异常应包含错误码和错误信息；Controller 层不捕获业务异常，由全局异常处理器统一处理。

### 项目构建

项目使用 Maven 管理构建，主要构建命令：

```bash
# 编译项目
mvn compile

# 运行测试
mvn test

# 打包（跳过测试）
mvn package -DskipTests

# 清理构建
mvn clean

# 完整构建（清理、编译、测试、打包）
mvn clean install

# 查看依赖树
mvn dependency:tree

# 分析依赖冲突
mvn dependency:analyze
```

### 测试方法

项目测试位于 src/test/java 目录，使用 JUnit 5 和 Spring Boot Test 框架。

**单元测试**：针对单个类或方法编写测试，验证其行为正确性。测试应覆盖正常路径和异常路径。示例测试类：NLPParserServiceTest、ChineseDateParserTest。

**集成测试**：测试多个组件协作的正确性。使用 @SpringBootTest 注解加载完整应用上下文。使用 @MockBean 模拟外部依赖。

**API 测试**：使用 MockMvc 或 RestAssured 测试 Controller 层的 HTTP 接口。验证请求参数、响应状态码和响应体内容。

运行测试：

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=NLPParserServiceTest

# 生成测试覆盖率报告
mvn test jacoco:report
```

## API使用示例

### 用户注册与登录

**注册新用户**

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "password123",
    "nickname": "约翰"
  }'
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 2,
      "username": "john",
      "email": "john@example.com",
      "nickname": "约翰"
    }
  }
}
```

**用户登录**

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

### 创建日程

**创建普通日程**

```bash
curl -X POST http://localhost:8080/api/v1/schedules \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "title": "项目周会",
    "description": "讨论本周工作进度",
    "startTime": "2024-01-20 10:00:00",
    "endTime": "2024-01-20 11:00:00",
    "type": "MEETING",
    "importance": 4,
    "location": "会议室A",
    "participants": ["张三", "李四"]
  }'
```

**创建带提醒的日程**

先创建日程，再为该日程添加提醒：

```bash
# 创建日程
SCHEDULE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/schedules \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "title": "重要会议",
    "startTime": "2024-01-25 14:00:00",
    "endTime": "2024-01-25 15:00:00",
    "type": "MEETING"
  }')

# 提取日程ID
SCHEDULE_ID=$(echo $SCHEDULE_RESPONSE | jq -r '.data.id')

# 创建提醒（提前30分钟）
curl -X POST http://localhost:8080/api/v1/reminders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d "{
    \"scheduleId\": $SCHEDULE_ID,
    \"type\": \"BEFORE\",
    \"minutes\": 30,
    \"enabled\": true
  }"
```

### NLP解析示例

**解析简单日程描述**

```bash
curl -X POST http://localhost:8080/api/v1/schedules/nlp \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "text": "明天下午三点和李四开会"
  }'
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "schedule": {
      "title": "和李四开会",
      "startTime": "2024-01-21 15:00:00",
      "endTime": "2024-01-21 16:00:00",
      "type": "MEETING",
      "participants": ["李四"]
    },
    "confidence": 0.92,
    "extractedEntities": {
      "participants": ["李四"],
      "time": "明天下午三点"
    }
  }
}
```

**解析并直接创建**

```bash
curl -X POST http://localhost:8080/api/v1/schedules/nlp/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "text": "下周一上午十点瑜伽课",
    "userId": 1
  }'
```

### 查询统计

**获取月度统计概览**

```bash
curl -X GET "http://localhost:8080/api/v1/statistics/overview?startDate=2024-01-01&endDate=2024-01-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalSchedules": 45,
    "completedSchedules": 38,
    "completionRate": 0.84,
    "onTimeRate": 0.92,
    "averageDuration": 65.5,
    "typeDistribution": {
      "MEETING": 15,
      "TASK": 20,
      "PERSONAL": 10
    }
  }
}
```

**获取时间分布**

```bash
curl -X GET "http://localhost:8080/api/v1/statistics/time-distribution?startDate=2024-01-01&endDate=2024-01-31&groupBy=DAY" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 标签管理

**创建标签**

```bash
curl -X POST http://localhost:8080/api/v1/tags \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "name": "项目A",
    "color": "#E6A23C"
  }'
```

**获取用户所有标签**

```bash
curl -X GET http://localhost:8080/api/v1/tags \
  -H "Authorization: Bearer ${TOKEN}"
```

### 冲突检测

**检测时间冲突**

```bash
curl -X POST http://localhost:8080/api/v1/schedules/check-conflict \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "startTime": "2024-01-20 10:00:00",
    "endTime": "2024-01-20 11:00:00"
  }'
```

响应示例（无冲突）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hasConflict": false,
    "conflicts": []
  }
}
```

响应示例（有冲突）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hasConflict": true,
    "conflicts": [
      {
        "scheduleId": 5,
        "title": "部门例会",
        "startTime": "2024-01-20 09:30:00",
        "endTime": "2024-01-20 10:30:00",
        "overlapType": "PARTIAL_OVERLAP",
        "overlapMinutes": 30
      }
    ]
  }
}
```

## 常见问题

### 数据库连接问题

**问题：应用启动时报错 "Communications link failure"**

这表示无法连接到 MySQL 数据库。排查步骤如下：

首先确认 MySQL 服务是否正在运行。在 Windows 上打开服务管理器检查 MySQL 服务状态，或执行 net start mysql 命令启动服务。Linux 或 Mac 上执行 systemctl status mysql 或 service mysql status 检查。

然后确认连接参数是否正确。检查 application.yml 中的 host、port、username、password 是否与 MySQL 配置一致。默认 MySQL 端口是 3306，如果修改过端口需要相应调整配置。

接着检查防火墙设置。确保 MySQL 端口（默认3306）未被防火墙阻止。如果数据库在远程服务器，需要在服务器防火墙上开放相应端口。

最后验证数据库连接。使用 MySQL 客户端工具（如 MySQL Workbench、Navicat）或命令行 mysql -u root -p 尝试连接，确认凭据正确。

**问题：执行数据库操作时报错 "Unknown database 'smart_schedule'"**

这表示指定的数据库不存在。需要先创建数据库。执行以下 SQL 命令创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS smart_schedule DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

或者运行项目中的初始化脚本 sql/init.sql，该脚本包含建库和建表语句。

**问题：报错 "Access denied for user 'root'@'localhost'"**

用户名或密码错误。请检查 application.yml 中的 username 和 password 配置是否与 MySQL 用户凭据一致。如果忘记密码，需要重置 MySQL root 密码。

### Redis连接问题

**问题：应用启动时报错 "Cannot connect to Redis server"**

这表示无法连接到 Redis 服务。排查步骤如下：

首先确认 Redis 服务是否正在运行。执行 redis-cli ping 命令，如果返回 PONG 表示 Redis 正常运行。如果返回 Connection refused，需要启动 Redis 服务。

然后检查 Redis 配置。确认 application.yml 中的 host、port、password（如果有）配置正确。Redis 默认端口是 6379。

接着检查 Redis 是否配置了密码认证。如果 Redis 配置了 requirepass，需要在配置文件中设置 spring.data.redis.password。

最后检查网络连接。使用 telnet localhost 6379 测试端口是否可达。如果 Redis 在远程服务器，需要确认网络连通性和防火墙设置。

**问题：使用 Redis 时报错 "READONLY You can't write against a read only replica"**

这通常发生在 Redis 主从复制环境中，应用程序连接到了只读从节点。检查 application.yml 中的 spring.data.redis 配置，确保连接的是主节点而不是从节点。

### JWT配置问题

**问题：请求返回 401 Unauthorized**

这表示认证失败。可能原因如下：

请求头中未携带令牌或令牌格式错误。正确的请求头格式为 Authorization: Bearer <token>，注意 Bearer 与令牌之间有空格。

令牌已过期。检查令牌的有效期配置，必要时重新登录获取新令牌。

令牌签名验证失败。检查 jwt.secret 配置是否正确，密钥必须与签发时使用的密钥一致。如果修改过密钥，所有现有令牌都将失效。

令牌在黑名单中。如果之前调用过登出接口，当前令牌会被加入黑名单。需要重新登录获取新令牌。

**问题：JWT 密钥配置建议**

生产环境中，JWT 密钥必须足够复杂且保密。建议使用以下方式生成安全的密钥：

使用代码生成随机密钥：可以使用 Hutool 的 IdUtil 或 Java 的 SecureRandom 生成随机字节，然后转换为 Base64 字符串作为密钥。

使用环境变量或配置中心管理密钥，不要将密钥硬编码在配置文件中或提交到代码仓库。可以使用 Kubernetes Secret、Consul ConfigVault 等工具管理敏感配置。

定期轮换密钥。更换密钥前需要通知所有用户重新登录。

### 其他常见问题

**问题：NLP 解析结果不准确**

自然语言解析的准确性取决于输入文本的清晰程度和完整性。提高解析准确性的建议：使用明确的日期和时间表达，如"2024年1月20日下午三点"；包含足够的情境信息，如"开会"而不是"讨论"；避免歧义表达，如同时提到多个日期或时间。

**问题：提醒未触发**

排查步骤如下：确认提醒的 enabled 状态为 true；检查关联日程的状态是否为 SCHEDULED 或 IN_PROGRESS，已完成或已取消的日程不会触发提醒；查看应用日志中 Quartz 相关的日志输出，确认 Job 是否被触发；检查 WebSocket 连接是否正常，如果使用 WebSocket 推送提醒通知。

**问题：统计报表数据不准确**

统计报表基于日程的 start_time、end_time 和 status 字段计算。确保日程状态的更新逻辑正确：完成日程时及时将状态改为 COMPLETED；取消日程时将状态改为 CANCELLED；日程状态更新不及时会导致统计数据滞后。

