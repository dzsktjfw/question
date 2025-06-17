package com.czw.question.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {
    /**
     * 教师编号
     */
    @TableId(type = IdType.AUTO)
    private Integer teacherId;

    /**
     * 姓名
     */
    private String name;
    /**
     * 加密密码
     */
    private String password;
    /**
     * 性别
     */
    private String gender;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 职称
     */
    private String title;

    /**
     * 聘任专业
     */
    private String major;

    /**
     * 专/兼职
     */
    private String employmentType;

    /**
     * 聘任时间
     */
    private LocalDate hireDate;

    /**
     * 联系电话
     */
    private String phone;


}
