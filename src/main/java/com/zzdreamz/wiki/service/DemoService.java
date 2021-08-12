package com.zzdreamz.wiki.service;

import com.zzdreamz.wiki.domain.Demo;
import com.zzdreamz.wiki.domain.DemoExample;
import com.zzdreamz.wiki.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService {

    @Autowired
    private DemoMapper domeMapper;

    public List<Demo> list(){
        return domeMapper.selectByExample(null);
    }
}
