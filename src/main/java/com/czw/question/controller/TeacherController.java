package com.czw.question.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.czw.question.pojo.Result;
import com.czw.question.pojo.dto.AnswerDto;
import com.czw.question.pojo.dto.UpdatePwdDto;
import com.czw.question.pojo.entity.Faq;
import com.czw.question.pojo.entity.ManualQa;
import com.czw.question.pojo.entity.Student;
import com.czw.question.pojo.entity.Teacher;
import com.czw.question.pojo.vo.WelcomeVo;
import com.czw.question.service.FaqService;
import com.czw.question.service.ManualQaService;
import com.czw.question.service.StudentService;
import com.czw.question.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/teacher")
@Slf4j
public class TeacherController {
    @Autowired
    private FaqService faqService;
    @Autowired
    private ManualQaService manualQaService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentService studentService;
    @GetMapping("/list")
    public Result<IPage<Faq>> list(@RequestParam("page") int page) {
        Page<Faq> pageParam = new Page<>(page, 10);
        QueryWrapper<Faq> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("query_count").orderByDesc("last_query_time");
        IPage<Faq> result = faqService.list(pageParam, queryWrapper);

        log.info("总页数：{}", result.getPages());
        log.info("总记录数：{}", result.getTotal());
        log.info("当前页记录数：{}", result.getRecords().size());

        return Result.success(result);
    }
    @GetMapping("/query")
    public Result<IPage<Faq>> query(@RequestParam("content") String content,@RequestParam("page") int page){
        QueryWrapper<Faq> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("content", content).orderByDesc("query_count").orderByDesc("last_query_time");
        IPage<Faq> result = faqService.list(new Page<>(page,10),queryWrapper);

        log.info("总页数：{}", result.getPages());
        log.info("总记录数：{}", result.getTotal());
        log.info("当前页记录数：{}", result.getRecords().size());

        return Result.success(result);
    }
    @PostMapping("/saveOrUpdate")
    private Result saveOrUpdate(@RequestParam("id") int id,@RequestParam("content") String content,@RequestParam("answer") String answer) {
        if(id==0){
            Faq faq = new Faq();
            faq.setContent(content);
            faq.setAnswer(answer);
            faq.setLastQueryTime(new Timestamp(System.currentTimeMillis()));
            faq.setQueryCount(0);
            faqService.save(faq);
        }else{
            Faq faq = faqService.getById(id);
            faq.setContent(content);
            faq.setAnswer(answer);
            faq.setLastQueryTime(new Timestamp(System.currentTimeMillis()));
            faqService.updateById(faq);
        }
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    private Result delete(@PathVariable Integer id) {
        faqService.removeById(id);
        return Result.success();
    }
    @GetMapping("/task")
    public Result<Long> getTask(){
        QueryWrapper<ManualQa> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_ok",0);
        long count = manualQaService.count(queryWrapper);
        return Result.success(count);
    }
    @GetMapping("/info/{id}")
    public Result<Teacher> info(@PathVariable("id") Integer id) {
        Teacher teacher = teacherService.getById(id);
        return Result.success(teacher);
    }
    @PutMapping("/updateinfo")
    private Result updateinfo(@RequestBody Teacher teacher) {
        teacherService.updateById(teacher);
        return Result.success();
    }
    @PostMapping("/updatepwd")
    private Result updatepwd(@RequestBody UpdatePwdDto updatePwdDto) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", updatePwdDto.getId())
                .eq("password",DigestUtil.md5Hex(updatePwdDto.getOldPassword()));
        Teacher teacher = teacherService.getOne(queryWrapper);
        if(teacher == null){
            return Result.error("原密码错误！");
        }
        teacher.setPassword(DigestUtil.md5Hex(updatePwdDto.getNewPassword()));
        teacherService.updateById(teacher);
        return Result.success();
    }
    @GetMapping("/show")
    public Result<IPage<ManualQa>> show(@RequestParam("page") int page,@RequestParam("size") int size,@RequestParam("only") int status){
        QueryWrapper<ManualQa> queryWrapper = new QueryWrapper<>();
        if(status == 1)
            queryWrapper.eq("is_ok", 0);
        IPage<ManualQa> res = manualQaService.show(new Page<>(page, size), queryWrapper);
        log.info("总页数：{}", res.getPages());
        log.info("总记录数：{}", res.getTotal());
        log.info("当前页记录数：{}", res.getRecords().size());
        return Result.success(res);
    }
    @PutMapping("/answer")
    private Result answer(@RequestBody AnswerDto answerDto) {
        UpdateWrapper<ManualQa> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("question_id", answerDto.getId()).set("answer",answerDto.getAnswer()).set("is_ok",1)
                .set("answer_time",new Timestamp(System.currentTimeMillis())).set("teacher_id",answerDto.getTeacherId()).set("teacher_name",answerDto.getTeacherName());
        manualQaService.update(updateWrapper);
        return Result.success();
    }
    @GetMapping("look")
    private Result<List<Student>> studentLook() {
        List<Student> list = studentService.list();
        return Result.success(list);
    }
    @PutMapping("/resetpwd/{id}")
    private Result resetpwd(@PathVariable Integer id) {
        UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("password", DigestUtil.md5Hex("111aaa"));
        studentService.update(updateWrapper);
        return Result.success();
    }
    @PutMapping("/stop/{id}")
    private Result stop(@PathVariable Integer id) {
        QueryWrapper<Student> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Student student = studentService.getOne(queryWrapper);
        boolean f= !student.getIsBlacklisted();
        student.setIsBlacklisted(f);
        studentService.updateById(student);
        return Result.success();
    }
    @GetMapping("/myanswers")
    public Result<IPage<ManualQa>> myanswer(@RequestParam int teacher_id,@RequestParam int limit,@RequestParam int offset) {
        QueryWrapper<ManualQa> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacher_id);
        IPage<ManualQa> ans = manualQaService.show(new Page<>(offset, limit), queryWrapper);
        return Result.success(ans);
    }
    @GetMapping("/welcome")
    public Result<WelcomeVo> welcome(@RequestParam int teacher_id) {
        long studentcount=studentService.count();
        // 获取今天的开始时间和结束时间
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        // 转换为 Date 类型（MyBatis-Plus 通常使用 Date）
        Date startTime = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endTime = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

        // 构建查询条件
        QueryWrapper<ManualQa> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacher_id)
                .ge("answer_time", startTime)  // >= 今天开始
                .lt("answer_time", endTime);   // < 今天结束
        long myanswer=manualQaService.count(queryWrapper);
        long questioncount=faqService.count();
        QueryWrapper<ManualQa> taskWrapper=new QueryWrapper<>();
        taskWrapper.eq("is_ok", 0);
        long taskcount=manualQaService.count(taskWrapper);
        WelcomeVo res = WelcomeVo.builder().StudentCount(studentcount)
                .TaskCount(taskcount)
                .TodayAnswer(myanswer)
                .QuestionCount(questioncount).build();
        return Result.success(res);
    }
}
