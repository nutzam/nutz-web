package org.nutz.web.query;

/**
 * 封装从 Web Client 来的字符串
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class WebQuery {

    public WebQuery() {
        this.order = getDefaultOrder();
        this.sortBy = getDefaultSortBy();
    }

    protected abstract String getDefaultSortBy();

    protected SORT getDefaultOrder() {
        return SORT.ASC;
    }

    protected String keyword;

    private SORT order;

    private String sortBy;

    /**
     * 小于等于0, 无视
     */
    private int limit;

    /**
     * 小于等于0, 无视
     */
    private int skip;

    public WebQuery sortBy(String fieldName) {
        sortBy = fieldName;
        return this;
    }

    public WebQuery asc() {
        order = SORT.ASC;
        return this;
    }

    public WebQuery desc() {
        order = SORT.DESC;
        return this;
    }

    public WebQuery limit(int limit) {
        this.limit = limit;
        return this;
    }

    public WebQuery skip(int skip) {
        this.skip = skip;
        return this;
    }

    public int limit() {
        return this.limit;
    }

    public int skip() {
        return this.skip;
    }

    public boolean isASC() {
        return SORT.ASC == order;
    }

    public boolean isDESC() {
        return SORT.DESC == order;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
        parseKeyword();
    }

    protected abstract void parseKeyword();

    public SORT getOrder() {
        return order;
    }

    public void setOrder(SORT order) {
        this.order = order;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
