package com.czw.question.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.czw.question.pojo.entity.Faq;

public interface FaqService extends IService<Faq> {
     IPage<Faq> list(Page<Faq> page, QueryWrapper<Faq> wrapper);
}
