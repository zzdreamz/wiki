package com.zzdreamz.wiki.controller;

import com.zzdreamz.wiki.domain.Ebook;
import com.zzdreamz.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EbookController {

    @Autowired
    private EbookService ebookService;

    @RequestMapping("/ebook/hello")
    public String hello() {
        return "Hello World, zzdreamz!!!";
    }

    @RequestMapping("/ebook/list")
    public List<Ebook> list() {
        return ebookService.list();
    }
}
