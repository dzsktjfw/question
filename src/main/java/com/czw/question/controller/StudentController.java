package com.czw.question.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.czw.question.pojo.Result;
import com.czw.question.pojo.entity.Faq;
import com.czw.question.pojo.entity.ManualQa;
import com.czw.question.service.FaqService;
import com.czw.question.service.ManualQaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;


@RestController
@RequestMapping("/student")
@Slf4j
public class StudentController {
    @Autowired
    private FaqService faqService;
    @Autowired
    private ManualQaService manualQaService;
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
    @GetMapping("/detail/{id}")
    public Result<Faq> detail(@PathVariable int id){
        Faq faq = faqService.getById(id);
        faq.setQueryCount(faq.getQueryCount()+1);
        faq.setLastQueryTime(new Timestamp(System.currentTimeMillis()));
        faqService.updateById(faq);
        return Result.success(faq);
    }

    @PostMapping("add")
    public Result add(@RequestBody ManualQa manualQa){
        manualQa.setAskTime(new Timestamp(System.currentTimeMillis()));
        manualQa.setIsOk(0);
        manualQaService.save(manualQa);
        return Result.success();
    }

    @GetMapping("/show")
    public Result<IPage<ManualQa>> show(@RequestParam("page") int page,@RequestParam("size") int size,@RequestParam("asker") String asker){
        QueryWrapper<ManualQa> queryWrapper = new QueryWrapper<>();
        if(asker!=null&&!asker.equals("")){
            queryWrapper.like("asker", asker);
        }
        IPage<ManualQa> res = manualQaService.show(new Page<>(page, size), queryWrapper);
        log.info("总页数：{}", res.getPages());
        log.info("总记录数：{}", res.getTotal());
        log.info("当前页记录数：{}", res.getRecords().size());
        return Result.success(res);
    }
}
