package com.zzdreamz.wiki.controller;

import com.zzdreamz.wiki.domain.Test;
import com.zzdreamz.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping("/test/hello")
    public String hello() {
        return "Hello World, zzdreamz!!!";
    }

    @RequestMapping("/test/list")
    public List<Test> list() {
        return testService.list();
    }
}
