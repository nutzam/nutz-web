package org.nutz.web.jsp.tld;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.NutMessageMap;

public class MsgTag extends BodyTagSupport {

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int doEndTag() throws JspException {
        // 得到 Message 对象
        NutMessageMap nmm = Mvcs.getMessageMap(pageContext.getRequest());

        // 准备上下文
        String body = this.getBodyContent().getString();
        NutMap map;
        if (!Strings.isBlank(body))
            map = Lang.map(body);
        else
            map = null;

        // 得到字符串
        String str = nmm.get(key, map);

        // 输出
        try {
            pageContext.getOut().write(str);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }

        return TagSupport.EVAL_PAGE;
    }
}
