package org.nutz.web.query;

import java.util.LinkedList;

import org.nutz.dao.QueryResult;

/**
 * 描述了一个分页查询结果
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class WebQueryResult {

    public int pn;

    public int pgsz;

    public int pgcount;

    public int rcount;

    public LinkedList<Object> list;

    public WebQueryResult() {
        list = new LinkedList<Object>();
    }

    public WebQueryResult(WebQuery q) {
        this();
        pn = q.getPageNumber();
        pgsz = q.getPageSize();
    }

    public WebQueryResult(QueryResult qr) {
        this();
        pn = qr.getPager().getPageNumber();
        pgsz = qr.getPager().getPageSize();
        pgcount = qr.getPager().getPageCount();
        rcount = qr.getPager().getRecordCount();
        list.addAll(qr.getList());
    }

    public WebQueryResult add(Object o) {
        list.add(o);
        return this;
    }
}
