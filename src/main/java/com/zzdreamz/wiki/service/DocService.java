package com.zzdreamz.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zzdreamz.wiki.domain.Content;
import com.zzdreamz.wiki.domain.Doc;
import com.zzdreamz.wiki.domain.DocExample;
import com.zzdreamz.wiki.mapper.ContentMapper;
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
    private ContentMapper contentMapper;


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

    public List<DocQueryResp> list(DocQueryReq req){
        // 添加查询条件
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andEbookIdEqualTo(req.getEbookId());
        List<Doc> docList = docMapper.selectByExample(docExample);
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        System.out.println(content);
        if (ObjectUtils.isEmpty(doc.getId())) {
            // 新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0) contentMapper.insert(content);
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        if (!ObjectUtils.isEmpty(content)) {
            return content.getContent();
        }
        return "";
    }
}
