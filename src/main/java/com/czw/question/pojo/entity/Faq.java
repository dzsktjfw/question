package com.czw.question.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.sql.Timestamp;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Date;

/**
 * 常见问题实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Faq {
    /**
     * 问题编号，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Integer questionId;

    /**
     * 问题内容
     */
    private String content;

    /**
     * 问题回答
     */
    private String answer;

    /**
     * 最后查询时间，默认为当前时间
     */
    private Timestamp lastQueryTime;

    /**
     * 查询次数，默认为0
     */
    private Integer queryCount;
}
