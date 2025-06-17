package com.czw.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czw.question.mapper.FaqMapper;
import com.czw.question.pojo.entity.Faq;
import com.czw.question.service.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaqServiceImpl extends ServiceImpl<FaqMapper, Faq> implements FaqService {
    @Autowired
    private FaqMapper faqMapper;


    @Override
    public IPage<Faq> list(Page<Faq> page, QueryWrapper<Faq> wrapper) {
        return faqMapper.selectPage(page, wrapper);
    }
}
