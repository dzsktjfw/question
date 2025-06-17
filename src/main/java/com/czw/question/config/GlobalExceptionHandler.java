package com.czw.question.config;


import com.czw.question.exception.BaseException;
import com.czw.question.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //Duplicate entry 'zhangsan' for key 'employee.idx_username
        String Message=ex.getMessage();
        if(Message.contains("Duplicate entry")){
            String[] s = Message.split(" ");
            String username=s[2];
            String msg=username+ "已经被注册";
            return Result.error(msg);
        }else {
            return Result.error("未知错误，请联系管理员");
        }
    }

}
