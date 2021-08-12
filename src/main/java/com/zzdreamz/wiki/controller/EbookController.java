package com.zzdreamz.wiki.controller;

import com.zzdreamz.wiki.domain.Ebook;
import com.zzdreamz.wiki.req.EbookReq;
import com.zzdreamz.wiki.resp.CommonResp;
import com.zzdreamz.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Autowired
    private EbookService ebookService;

    @RequestMapping("/list")
    public CommonResp list(EbookReq req) {
        CommonResp<List<Ebook>> resp = new CommonResp<>();
        List<Ebook> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
}
