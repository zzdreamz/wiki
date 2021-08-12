package com.zzdreamz.wiki.service;

import com.zzdreamz.wiki.domain.Ebook;
import com.zzdreamz.wiki.domain.EbookExample;
import com.zzdreamz.wiki.mapper.EbookMapper;
import com.zzdreamz.wiki.req.EbookReq;
import com.zzdreamz.wiki.resp.EbookResp;
import com.zzdreamz.wiki.util.CopyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    @Autowired
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req){
        // 添加查询条件
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        criteria.andNameLike("%" + req.getName() + "%");
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        return CopyUtil.copyList(ebookList, EbookResp.class);
    }
}
