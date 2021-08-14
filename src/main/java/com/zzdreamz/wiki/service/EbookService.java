package com.zzdreamz.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zzdreamz.wiki.domain.Ebook;
import com.zzdreamz.wiki.domain.EbookExample;
import com.zzdreamz.wiki.mapper.EbookMapper;
import com.zzdreamz.wiki.req.EbookQueryReq;
import com.zzdreamz.wiki.req.EbookSaveReq;
import com.zzdreamz.wiki.resp.EbookQueryResp;
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
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Autowired
    private EbookMapper ebookMapper;

    @Autowired
    private SnowFlake snowFlake;

    public PageResp<EbookQueryResp> list(EbookQueryReq req){
        // 添加查询条件
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        if (!ObjectUtils.isEmpty(req.getCategory2Id())) {
            criteria.andCategory2IdEqualTo(req.getCategory2Id());
        }
        // pageHelper分页查询
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);

        // 使用PageResp来封装返回对象
        PageResp<EbookQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if (ObjectUtils.isEmpty(ebook.getId())) {
            // 新增
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        } else {
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    public void delete(Long id) {
        ebookMapper.deleteByPrimaryKey(id);
    }
}
