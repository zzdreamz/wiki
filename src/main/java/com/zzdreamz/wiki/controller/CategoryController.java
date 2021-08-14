package com.zzdreamz.wiki.controller;

import com.zzdreamz.wiki.req.CategoryQueryReq;
import com.zzdreamz.wiki.req.CategorySaveReq;
import com.zzdreamz.wiki.resp.CommonResp;
import com.zzdreamz.wiki.resp.CategoryQueryResp;
import com.zzdreamz.wiki.resp.PageResp;
import com.zzdreamz.wiki.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req) {
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> pageResp = categoryService.list(req);
        resp.setContent(pageResp);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq req) {
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }
}
