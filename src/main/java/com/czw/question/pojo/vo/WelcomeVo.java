package com.czw.question.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WelcomeVo {
    private Long StudentCount;
    private Long TodayAnswer;
    private Long QuestionCount;
    private Long TaskCount;
}
