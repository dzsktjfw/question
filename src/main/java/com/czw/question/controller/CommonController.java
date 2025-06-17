package com.czw.question.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.czw.question.pojo.dto.LoginForm;
import com.czw.question.pojo.Result;
import com.czw.question.pojo.entity.Student;
import com.czw.question.pojo.entity.Teacher;
import com.czw.question.properity.JwtProperity;
import com.czw.question.service.StudentService;
import com.czw.question.service.TeacherService;
import com.czw.question.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class CommonController {

    @Autowired
    StudentService studentService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    private JwtProperity jwtProperties;
    @PostMapping("/register")
    public Result register(@RequestBody LoginForm user) {
        log.info("注册......");
        if(user.getRole().equals("student")){
            Student student = BeanUtil.copyProperties(user,Student.class);
            student.setPassword(DigestUtil.md5Hex(student.getPassword()));
            studentService.save(student);
            return Result.success();
        }else{
            Teacher teacher = new Teacher();
            teacher.setName(user.getUsername());
            teacher.setPassword(DigestUtil.md5Hex(user.getPassword()));
            teacherService.save(teacher);
            return Result.success();
        }
    }
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginForm user) {
        log.info("登录......");
        if(user.getRole().equals("student")){
            QueryWrapper<Student> wrapper = new QueryWrapper<Student>()
                    .select("id", "username","password", "is_blacklisted", "created_at","updated_at")
                    .eq("username", user.getUsername())
                    .eq("password", DigestUtil.md5Hex(user.getPassword()));
            List<Student> list = studentService.list(wrapper);
            if(list.isEmpty()){
                return Result.error("用户名或密码错误");
            }
            Student student = list.get(0);
            if(student.getIsBlacklisted())
                return Result.error("你已被拉入黑名单");
            //登录成功后，生成jwt令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", student.getId());
            claims.put("username", student.getUsername());
            claims.put("role","student");
            System.out.println("login..."+claims);
            String token = JwtUtil.createJWT(
                    jwtProperties.getSecretKey(),
                    jwtProperties.getTtl(),
                    claims);
            return Result.success(token);
        }else{
            QueryWrapper<Teacher> wrapper = new QueryWrapper<Teacher>()
                    .select("teacher_id", "name","password")
                    .eq("name", user.getUsername())
                    .eq("password", DigestUtil.md5Hex(user.getPassword()));
            List<Teacher> list = teacherService.list(wrapper);
            if(list.isEmpty()){
                return Result.error("用户名或密码错误");
            }
            Teacher teacher = list.get(0);
            //登录成功后，生成jwt令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", teacher.getTeacherId());
            claims.put("username", teacher.getName());
            claims.put("role","teacher");
            System.out.println("login..."+claims);
            String token = JwtUtil.createJWT(
                    jwtProperties.getSecretKey(),
                    jwtProperties.getTtl(),
                    claims);
            return Result.success(token);
        }
    }
}
