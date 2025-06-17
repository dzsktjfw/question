package com.czw.question.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Student {
    @TableId(type = IdType.AUTO)
    private Long id;           // 学生唯一标识符
    private String username;   // 用户名（学号/登录账号）
    private String password;   // 加密密码
    private Boolean isBlacklisted; // 黑名单状态（0-正常 1-黑名单）
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}
