package org.nutz.web.fliter;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.ServerRedirectView;

/**
 * 查看session是否包含特定的属性,来判断是否已经登陆,如果没有登陆,则直接跳转至特定url
 * 
 * @author pw
 * 
 */
public class CheckNotLogin implements ActionFilter {

    private String name;
    private String path;

    public CheckNotLogin(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public View match(ActionContext context) {
        Object obj = context.getRequest().getSession().getAttribute(name);
        if (null == obj)
            return new ServerRedirectView(path);
        return null;
    }

}