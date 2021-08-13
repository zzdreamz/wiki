package com.zzdreamz.wiki.resp;

import java.util.List;

public class PageResp<T> {

    private long Total;

    private List<T> list;

    public PageResp() {
    }

    public PageResp(long total, List<T> list) {
        Total = total;
        this.list = list;
    }

    public long getTotal() {
        return Total;
    }

    public void setTotal(long total) {
        Total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageResp{" +
                "Total=" + Total +
                ", list=" + list +
                '}';
    }
}
