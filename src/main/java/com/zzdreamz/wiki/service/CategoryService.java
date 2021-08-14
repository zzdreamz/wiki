package com.zzdreamz.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zzdreamz.wiki.domain.Category;
import com.zzdreamz.wiki.domain.CategoryExample;
import com.zzdreamz.wiki.mapper.CategoryMapper;
import com.zzdreamz.wiki.req.CategoryQueryReq;
import com.zzdreamz.wiki.req.CategorySaveReq;
import com.zzdreamz.wiki.resp.CategoryQueryResp;
import com.zzdreamz.wiki.resp.PageResp;
import com.zzdreamz.wiki.util.CopyUtil;
import com.zzdreamz.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SnowFlake snowFlake;

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req){
        // pageHelper分页查询
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<Category> categoryList = categoryMapper.selectByExample(null);
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        // 使用PageResp来封装返回对象
        PageResp<CategoryQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if (ObjectUtils.isEmpty(category.getId())) {
            // 新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }
}
