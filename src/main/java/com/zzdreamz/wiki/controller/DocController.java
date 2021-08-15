package com.zzdreamz.wiki.controller;

import com.zzdreamz.wiki.req.DocQueryReq;
import com.zzdreamz.wiki.req.DocSaveReq;
import com.zzdreamz.wiki.resp.DocQueryResp;
import com.zzdreamz.wiki.resp.CommonResp;
import com.zzdreamz.wiki.resp.PageResp;
import com.zzdreamz.wiki.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired
    private DocService docService;

    @GetMapping("/all")
    public CommonResp all() {
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all();
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req) {
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> pageResp = docService.list(req);
        resp.setContent(pageResp);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req) {
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        docService.delete(id);
        return resp;
    }
}
