create DATABASE question;
CREATE TABLE `teacher` (
  `teacher_id` int NOT NULL AUTO_INCREMENT COMMENT '教师编号',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `password` varchar(255) NOT NULL,
  `gender` enum('男','女') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '性别',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '职称',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '聘任专业',
  `employment_type` enum('专职','兼职') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '专职' COMMENT '专/兼职',
  `hire_date` date DEFAULT NULL COMMENT '聘任时间',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系电话',
  PRIMARY KEY (`teacher_id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_major` (`major`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教师基本信息表';
CREATE TABLE `student` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '学生唯一标识符',
  `username` varchar(50) NOT NULL COMMENT '用户名（学号/登录账号）',
  `password` varchar(255) NOT NULL COMMENT '加密密码',
  `is_blacklisted` tinyint(1) DEFAULT '0' COMMENT '黑名单状态（0-正常 1-黑名单）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生账户信息表';
CREATE TABLE `faq` (
  `question_id` int NOT NULL AUTO_INCREMENT COMMENT '问题编号',
  `content` text NOT NULL COMMENT '问题内容',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题回答',
  `last_query_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后查询时间',
  `query_count` int DEFAULT '0' COMMENT '查询次数',
  PRIMARY KEY (`question_id`),
  FULLTEXT KEY `ft_content` (`content`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='常见问题库表';
CREATE TABLE `manual_qa` (
  `question_id` int NOT NULL AUTO_INCREMENT COMMENT '问题编号',
  `asker` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '提出人(用户名)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '提问内容',
  `ask_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提出时间',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '教师回答',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '回答教师姓名',
  `answer_time` datetime DEFAULT NULL COMMENT '回答时间',
  `teacher_id` int DEFAULT NULL COMMENT '教师编号',
  `is_ok` tinyint NOT NULL COMMENT '是否已解决',
  PRIMARY KEY (`question_id`),
  KEY `teacher_id` (`teacher_id`),
  CONSTRAINT `manual_qa_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`teacher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人工答疑记录表';