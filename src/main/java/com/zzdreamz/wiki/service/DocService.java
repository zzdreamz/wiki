package com.zzdreamz.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zzdreamz.wiki.domain.Doc;
import com.zzdreamz.wiki.domain.DocExample;
import com.zzdreamz.wiki.mapper.DocMapper;
import com.zzdreamz.wiki.req.DocQueryReq;
import com.zzdreamz.wiki.req.DocSaveReq;
import com.zzdreamz.wiki.resp.DocQueryResp;
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
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Autowired
    private DocMapper docMapper;

    @Autowired
    private SnowFlake snowFlake;

    public List<DocQueryResp> all(){
        // 添加查询条件
        DocExample docExample = new DocExample();
        // 排序
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req){
        // 添加查询条件
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        // pageHelper分页查询
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<Doc> docList = docMapper.selectByExample(docExample);
        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        // 使用PageResp来封装返回对象
        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        if (ObjectUtils.isEmpty(doc.getId())) {
            // 新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        } else {
            docMapper.updateByPrimaryKey(doc);
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }
}
