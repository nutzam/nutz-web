package org.nutz.web.processor;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.ViewProcessor;

public class NoPrintFailProcessor extends ViewProcessor {

    private static final Log log = Logs.get();

    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        view = evalView(config, ai, ai.getFailView());
    }

    public void process(ActionContext ac) throws Throwable {
        if (log.isWarnEnabled()) {
            String uri = Mvcs.getRequestPath(ac.getRequest());
            log.warnf("Erro@%s : '%s'", uri, ac.getError().toString());
        }
        super.process(ac);
    }
}