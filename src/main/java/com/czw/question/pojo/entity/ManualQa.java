package com.czw.question.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 人工答疑记录表实体类
 * 对应数据库表：manual_qa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManualQa {
    /**
     * 问题编号（主键，自增长）
     */
    @TableId(type = IdType.AUTO)
    private Integer questionId;

    /**
     * 提出人（学生学号/姓名）
     */
    private String asker;


    private String content;


    private String answer;

    /**
     * 提出时间（插入时自动生成）
     */
    private Timestamp askTime;

    /**
     * 回答教师姓名
     */
    private String teacherName;

    /**
     * 回答时间
     */
    private Timestamp answerTime;

    /**
     * 教师编号（外键关联teacher表）
     */
    private Integer teacherId;

    private Integer isOk;

}