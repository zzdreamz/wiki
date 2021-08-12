package com.zzdreamz.wiki.controller;

import com.zzdreamz.wiki.domain.Demo;
import com.zzdreamz.wiki.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DemoController {

    @Autowired
    private DemoService demoService;

    @RequestMapping("/demo/hello")
    public String hello() {
        return "Hello World, zzdreamz!!!";
    }

    @RequestMapping("/demo/list")
    public List<Demo> list() {
        return demoService.list();
    }
}
