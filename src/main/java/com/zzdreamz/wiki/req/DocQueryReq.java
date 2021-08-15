package com.zzdreamz.wiki.req;

import javax.validation.constraints.NotNull;

public class DocQueryReq extends PageReq {

    @NotNull(message = "【电子书Id】不能为空")
    private Long ebookId;

    public DocQueryReq() {
    }

    public DocQueryReq(int pageNum, int pageSize) {
        super(pageNum, pageSize);
    }

    public DocQueryReq(Long ebookId) {
        this.ebookId = ebookId;
    }

    public DocQueryReq(int pageNum, int pageSize, Long ebookId) {
        super(pageNum, pageSize);
        this.ebookId = ebookId;
    }

    public Long getEbookId() {
        return ebookId;
    }

    public void setEbookId(Long ebookId) {
        this.ebookId = ebookId;
    }

    @Override
    public String toString() {
        return "DocQueryReq{" +
                "ebookId=" + ebookId +
                "} " + super.toString();
    }
}