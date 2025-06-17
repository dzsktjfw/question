package com.czw.question.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czw.question.mapper.TeacherMapper;
import com.czw.question.pojo.entity.Teacher;
import com.czw.question.service.TeacherService;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
}
