SET NAMES utf8mb4;
USE smart_schedule;

-- 确保 todos 表存在（JPA 启动后创建，但初始化阶段先手动创建）
CREATE TABLE IF NOT EXISTS todos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    important BIT(1) NOT NULL DEFAULT 0,
    completed BIT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户（密码统一: 123456）
INSERT INTO users (username, email, password_hash, nickname, timezone, created_at, updated_at) VALUES
('testuser','test@example.com','$2b$10$OITW2DoixgORTWMsJ3NmNOy/vOXvVyvuyHEPVrTQqhjMOgVx3ErKi','测试用户','Asia/Shanghai',NOW(),NOW()),
('zhangsan','zhangsan@example.com','$2b$10$OITW2DoixgORTWMsJ3NmNOy/vOXvVyvuyHEPVrTQqhjMOgVx3ErKi','张三','Asia/Shanghai',NOW(),NOW()),
('admin','admin@example.com','$2b$10$OITW2DoixgORTWMsJ3NmNOy/vOXvVyvuyHEPVrTQqhjMOgVx3ErKi','管理员','Asia/Shanghai',NOW(),NOW());

-- 标签
INSERT INTO schedule_tags (user_id, name, color, icon, created_at) VALUES
(1,'工作','#409EFF','work',NOW()),
(1,'学习','#67C23A','study',NOW()),
(1,'生活','#E6A23C','life',NOW()),
(1,'健康','#F56C6C','health',NOW()),
(1,'重要','#909399','important',NOW());

-- 日程
INSERT INTO schedules (user_id, title, description, start_time, end_time, duration_minutes, type, status, importance, location, participants, source, important, created_at, updated_at) VALUES
(1,'上午团队站会','与前端团队同步本周开发进度','2026-05-20 09:00:00','2026-05-20 09:30:00',30,'MEETING','SCHEDULED',3,'3楼会议室A','["李工","王工"]','MANUAL',0,NOW(),NOW()),
(1,'产品评审会议','评审新版本功能，确定迭代计划','2026-05-20 14:00:00','2026-05-20 16:00:00',120,'MEETING','SCHEDULED',5,'5楼大会议室','["产品部","开发部"]','MANUAL',0,NOW(),NOW());

INSERT INTO schedules (user_id, title, description, start_time, end_time, duration_minutes, type, status, importance, location, source, important, created_at, updated_at) VALUES
(1,'代码审查','审查同事PR，检查代码规范','2026-05-21 09:00:00','2026-05-21 10:30:00',90,'TASK','SCHEDULED',3,NULL,'MANUAL',0,NOW(),NOW()),
(1,'紧急客户会议','与代码审查重叠，触发冲突检测','2026-05-21 09:30:00','2026-05-21 11:00:00',90,'MEETING','SCHEDULED',5,'贵宾接待室','MANUAL',0,NOW(),NOW());

INSERT INTO schedules (user_id, title, description, start_time, end_time, duration_minutes, type, status, importance, source, important, created_at, updated_at) VALUES
(1,'提交周报','本周工作总结+下周计划','2026-05-22 16:00:00','2026-05-22 17:00:00',60,'TASK','SCHEDULED',4,'MANUAL',0,NOW(),NOW());

INSERT INTO schedules (user_id, title, description, start_time, end_time, duration_minutes, type, status, importance, location, source, important, created_at, updated_at) VALUES
(1,'朋友聚餐','大学同学聚餐','2026-05-23 18:00:00','2026-05-23 21:00:00',180,'PERSONAL','SCHEDULED',2,'南山火锅','MANUAL',0,NOW(),NOW()),
(1,'梧桐山徒步','部门团建登山活动','2026-05-24 08:00:00','2026-05-24 17:00:00',540,'TRAVEL','SCHEDULED',2,'梧桐山','MANUAL',0,NOW(),NOW());

INSERT INTO schedules (user_id, title, description, start_time, end_time, duration_minutes, type, status, importance, location, source, important, created_at, updated_at) VALUES
(1,'项目周例会','每周进度同步会','2026-05-25 09:00:00','2026-05-25 10:00:00',60,'MEETING','SCHEDULED',4,'3楼会议室A','MANUAL',0,NOW(),NOW()),
(1,'健身计划','有氧+力量训练','2026-05-26 18:00:00','2026-05-26 19:00:00',60,'PERSONAL','SCHEDULED',2,'公司健身房','MANUAL',0,NOW(),NOW()),
(1,'前端技术分享','Vue3 Composition API实践','2026-05-27 15:00:00','2026-05-27 16:30:00',90,'MEETING','SCHEDULED',3,'培训室','MANUAL',0,NOW(),NOW()),
(1,'季度总结截止','Q2季度工作总结提交','2026-05-28 10:00:00','2026-05-28 10:30:00',30,'DEADLINE','SCHEDULED',5,NULL,'MANUAL',0,NOW(),NOW()),
(1,'团队聚餐','月末团建聚餐','2026-05-29 18:00:00','2026-05-29 20:00:00',120,'PERSONAL','SCHEDULED',2,'湘味楼','MANUAL',0,NOW(),NOW());

INSERT INTO schedules (user_id, title, description, start_time, end_time, duration_minutes, type, status, importance, participants, source, raw_text, important, created_at, updated_at) VALUES
(1,'讨论技术方案','和张三讨论新系统技术选型','2026-05-27 10:00:00','2026-05-27 11:00:00',60,'MEETING','SCHEDULED',3,'["张三"]','NLP','下周三十点和张三讨论技术方案',0,NOW(),NOW());

INSERT INTO schedules (user_id, title, description, start_time, end_time, duration_minutes, type, status, importance, source, important, created_at, updated_at) VALUES
(1,'上周项目总结会','总结上周工作安排本周任务','2026-05-13 10:00:00','2026-05-13 11:00:00',60,'MEETING','COMPLETED',3,'MANUAL',0,NOW(),NOW()),
(1,'新员工培训','入职培训（已取消）','2026-05-15 14:00:00','2026-05-15 17:00:00',180,'OTHER','CANCELLED',2,'MANUAL',0,NOW(),NOW());

INSERT INTO schedules (user_id, title, description, start_time, end_time, duration_minutes, type, status, importance, location, source, important, created_at, updated_at) VALUES
(1,'端午假期','端午节三天出游','2026-06-12 08:00:00','2026-06-14 22:00:00',60,'TRAVEL','SCHEDULED',3,'厦门','MANUAL',0,NOW(),NOW()),
(1,'半年总结会','上半年工作总结及下半年规划','2026-06-30 14:00:00','2026-06-30 17:00:00',180,'MEETING','SCHEDULED',5,'总部大会议室','MANUAL',0,NOW(),NOW());

-- 待办
INSERT INTO todos (user_id, title, important, completed, created_at, updated_at) VALUES
(1,'购买生日礼物',1,0,NOW(),NOW()),
(1,'预约牙科诊所',1,0,NOW(),NOW()),
(1,'给导师发邮件汇报进展',0,0,NOW(),NOW()),
(1,'更换空调滤芯',0,0,NOW(),NOW()),
(1,'制定下月学习计划',1,0,NOW(),NOW()),
(1,'整理读书笔记',0,1,NOW(),NOW()),
(1,'检查信用卡账单',0,1,NOW(),NOW()),
(1,'预约车辆保养',0,0,NOW(),NOW());

-- 日程-标签关联
INSERT INTO schedule_tag_relation (schedule_id, tag_id) VALUES
(1,1),(2,1),(2,5),(3,2),(4,1),(4,5),(5,1),
(6,3),(7,4),(7,3),(8,1),(9,4),(10,2),(11,1),(11,5),
(12,3),(13,1),(14,3),(15,1),(16,1),(16,5);

-- 提醒
INSERT INTO reminders (schedule_id, type, offset_minutes, status, scheduled_time, channel) VALUES
(1,'NOTIFICATION',15,'PENDING','2026-05-20 08:45:00','APP'),
(2,'NOTIFICATION',1440,'PENDING','2026-05-19 14:00:00','APP'),
(2,'NOTIFICATION',60,'PENDING','2026-05-20 13:00:00','APP'),
(2,'NOTIFICATION',15,'PENDING','2026-05-20 13:45:00','APP'),
(4,'NOTIFICATION',30,'PENDING','2026-05-21 08:30:00','APP'),
(8,'NOTIFICATION',30,'PENDING','2026-05-25 08:30:00','APP'),
(11,'NOTIFICATION',1440,'PENDING','2026-05-27 10:00:00','APP');

-- 用户画像
INSERT INTO user_profiles (user_id, avg_schedule_duration, most_active_hours, working_hours_start, working_hours_end, preferred_meeting_duration) VALUES
(1,60,'[9,10,14,15,16]','09:00:00','18:00:00',60);

SELECT CONCAT('users:',COUNT(*)) AS '' FROM users;
SELECT CONCAT('schedules:',COUNT(*)) AS '' FROM schedules WHERE user_id=1;
SELECT CONCAT('todos:',COUNT(*)) AS '' FROM todos WHERE user_id=1;
SELECT CONCAT('tags:',COUNT(*)) AS '' FROM schedule_tags WHERE user_id=1;