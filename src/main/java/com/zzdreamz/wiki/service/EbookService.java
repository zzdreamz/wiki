package com.zzdreamz.wiki.service;

import com.zzdreamz.wiki.domain.Ebook;
import com.zzdreamz.wiki.domain.EbookExample;
import com.zzdreamz.wiki.mapper.EbookMapper;
import com.zzdreamz.wiki.req.EbookReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EbookService {

    @Autowired
    private EbookMapper ebookMapper;

    public List<Ebook> list(EbookReq req){
        // 添加查询条件
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        criteria.andNameLike("%" + req.getName() + "%");
        return ebookMapper.selectByExample(ebookExample);
    }
}
