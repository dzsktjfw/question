package com.czw.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czw.question.mapper.ManualQaMapper;
import com.czw.question.pojo.entity.ManualQa;
import com.czw.question.service.ManualQaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManualQaServiceImpl extends ServiceImpl<ManualQaMapper, ManualQa> implements ManualQaService {
    @Autowired
    private ManualQaMapper manualQaMapper;
    @Override
    public IPage<ManualQa> show(Page<ManualQa> page, QueryWrapper<ManualQa> wrapper) {
        return manualQaMapper.selectPage(page, wrapper);
    }
}
