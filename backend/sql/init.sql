CREATE DATABASE IF NOT EXISTS smart_schedule DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE smart_schedule;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '手机号',
    timezone VARCHAR(50) DEFAULT 'Asia/Shanghai' COMMENT '时区',
    preferences JSON COMMENT '用户偏好设置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE INDEX uk_username (username),
    UNIQUE INDEX uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS schedules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日程ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '日程标题',
    description TEXT COMMENT '详细描述',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    duration_minutes INT COMMENT '持续时长(分钟)',
    type VARCHAR(20) NOT NULL COMMENT '日程类型',
    status VARCHAR(20) DEFAULT 'SCHEDULED' COMMENT '日程状态',
    importance TINYINT DEFAULT 3 COMMENT '重要程度1-5',
    location VARCHAR(200) COMMENT '地点',
    participants JSON COMMENT '参与人列表',
    repeat_rule JSON COMMENT '重复规则',
    source VARCHAR(20) DEFAULT 'MANUAL' COMMENT '来源',
    raw_text VARCHAR(1000) COMMENT '原始输入文本',
    extensions JSON COMMENT '扩展字段',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_time (user_id, start_time, end_time),
    INDEX idx_user_status (user_id, status),
    INDEX idx_type (type),
    CONSTRAINT fk_schedule_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程表';

CREATE TABLE IF NOT EXISTS schedule_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    color VARCHAR(20) DEFAULT '#409EFF' COMMENT '标签颜色',
    icon VARCHAR(50) COMMENT '标签图标',
    UNIQUE INDEX uk_user_tag (user_id, name),
    CONSTRAINT fk_tag_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程标签表';

CREATE TABLE IF NOT EXISTS schedule_tag_relation (
    schedule_id BIGINT NOT NULL COMMENT '日程ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (schedule_id, tag_id),
    CONSTRAINT fk_relation_schedule FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE CASCADE,
    CONSTRAINT fk_relation_tag FOREIGN KEY (tag_id) REFERENCES schedule_tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程标签关联表';

CREATE TABLE IF NOT EXISTS reminders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提醒ID',
    schedule_id BIGINT NOT NULL COMMENT '日程ID',
    type VARCHAR(20) NOT NULL COMMENT '提醒方式',
    offset_minutes INT NOT NULL COMMENT '提前分钟数',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '提醒状态',
    scheduled_time DATETIME NOT NULL COMMENT '计划提醒时间',
    actual_time DATETIME COMMENT '实际提醒时间',
    channel VARCHAR(20) COMMENT '通知渠道',
    recipient VARCHAR(100) COMMENT '接收人',
    response JSON COMMENT '响应数据',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_schedule (schedule_id),
    INDEX idx_status_scheduled (status, scheduled_time),
    CONSTRAINT fk_reminder_schedule FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提醒记录表';

CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    avg_schedule_duration INT DEFAULT 60 COMMENT '平均日程时长(分钟)',
    most_active_hours JSON COMMENT '最活跃时段',
    common_locations JSON COMMENT '常去地点',
    frequent_contacts JSON COMMENT '频繁联系人',
    working_hours_start TIME COMMENT '工作开始时间',
    working_hours_end TIME COMMENT '工作结束时间',
    preferred_meeting_duration INT COMMENT '偏好会议时长',
    tag_preferences JSON COMMENT '标签偏好',
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户画像表';