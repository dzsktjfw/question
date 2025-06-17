package com.czw.question.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czw.question.mapper.StudentMapper;
import com.czw.question.pojo.entity.Student;
import com.czw.question.service.StudentService;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper,Student> implements StudentService {
}
