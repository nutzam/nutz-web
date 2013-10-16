package org.nutz.web.error;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

/**
 * 
 * 如果发现exception,打印出对应的信息
 * 
 * @author pw
 * 
 */
public class ErrPageViewMaker implements ViewMaker {

    @Override
    public View make(Ioc ioc, String type, String value) {
        if ("errpage".equalsIgnoreCase(type)) {
            return new ErrPageView();
        }
        return null;
    }

}
