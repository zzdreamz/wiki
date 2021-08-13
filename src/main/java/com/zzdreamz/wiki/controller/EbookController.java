package com.zzdreamz.wiki.controller;

import com.zzdreamz.wiki.req.EbookQueryReq;
import com.zzdreamz.wiki.req.EbookSaveReq;
import com.zzdreamz.wiki.resp.CommonResp;
import com.zzdreamz.wiki.resp.EbookQueryResp;
import com.zzdreamz.wiki.resp.PageResp;
import com.zzdreamz.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Autowired
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookQueryReq req) {
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> pageResp = ebookService.list(req);
        resp.setContent(pageResp);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody EbookSaveReq req) {
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }
}
