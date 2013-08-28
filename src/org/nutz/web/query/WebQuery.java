package org.nutz.web.query;

import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.Param;

/**
 * 封装从 Web Client 来的查询字符串，它接受如下格式的名值对或者 JSON 字符串
 * 
 * <pre>
 *  kwd   : "xxxxxx",
 *  pn    : 1,         // 当前页，<=0 被认为非法，强制等于 1
 *  pgsz  : 50,        // 一页多少数据，如果 <1 则强制等于 50
 *  order : "ASC:$name,..."    // 根据字段来排序
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class WebQuery {

    @Param("kwd")
    private String keyword;

    @Param("pn")
    private int pageNumber;

    @Param("pgsz")
    private int pageSize;

    @Param("order")
    private String order;

    private WebOrderField[] orderFields;

    public boolean hasKeyword() {
        return !Strings.isBlank(keyword);
    }

    public char[] getKeywordChars() {
        if (null != keyword)
            return keyword.toCharArray();
        return new char[0];
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String kwd) {
        this.keyword = kwd;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pn) {
        this.pageNumber = pn;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pgsz) {
        this.pageSize = pgsz;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
        String[] ss = Strings.splitIgnoreBlank(order, ",");
        if (null != ss) {
            orderFields = new WebOrderField[ss.length];
            for (int i = 0; i < ss.length; i++) {
                orderFields[i] = WebOrderField.valueOf(ss[i]);
            }
        }
    }

    public WebOrderField[] getOrderFields() {
        return orderFields;
    }

    public void setOrderFields(WebOrderField[] orderFields) {
        this.orderFields = orderFields;
    }

}
