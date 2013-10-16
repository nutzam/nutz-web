package org.nutz.web.error;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.segment.CharSegment;
import org.nutz.mvc.View;
import org.nutz.web.maker.WebMaker;

public class ErrPageView implements View {

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws Throwable {
        String errMsg = null;
        if (obj instanceof Throwable) {
            String tmpl = Streams.readAndClose(new InputStreamReader(WebMaker.class.getResourceAsStream("/org/nutz/web/error/errpage.html.tmpl")));
            CharSegment cs = new CharSegment(tmpl);
            // 异常
            errMsg = Lang.getStackTrace((Throwable) obj);
            String[] errlines = errMsg.split("\n");
            StringBuilder sb = new StringBuilder();
            Map<String, Integer> pkgNum = new HashMap<String, Integer>();
            int cnum = 0;
            for (int i = 0; i < errlines.length; i++) {
                if (i == 0) {
                    cs.set("exInfo", errlines[i]);
                } else {
                    sb.append("<div class='ex-stack'>");
                    String el = errlines[i].trim().substring(3);
                    // 判断包
                    String pkg = pkg(el);
                    String color = null;
                    if (!pkgNum.containsKey(pkg)) {
                        pkgNum.put(pkg, cnum);
                        cnum++;
                    }
                    color = "c" + pkgNum.get(pkg);
                    sb.append("at");
                    sb.append("<div class='stack-at " + color + "'>");
                    sb.append(at(el));
                    sb.append("</div>");
                    // 获取方法
                    sb.append("(");
                    sb.append("<div class='stack-method'>");
                    sb.append(method(el));
                    sb.append("</div>");
                    // 获得行号
                    int ln = methodLinenum(el);
                    if (ln > 0) {
                        sb.append(":");
                        sb.append("<div class='stack-method-line'>");
                        sb.append(ln);
                        sb.append("</div>");
                    }
                    sb.append(")");
                    sb.append("</div>");
                }
            }
            cs.set("exStack", sb.toString());
            errMsg = cs.render().toString();
        } else {
            // 其他的东西..
            errMsg = Json.toJson(obj);
        }
        // 输出到模板中
        PrintWriter respWriter = resp.getWriter();
        respWriter.write(errMsg);
        respWriter.flush();
    }

    private String pkg(String el) {
        int dotNum = 0;
        StringBuilder pkg = new StringBuilder();
        for (int i = 0; i < el.length(); i++) {
            if (el.charAt(i) == '.') {
                dotNum++;
            }
            if (dotNum >= 3) {
                break;
            }
            pkg.append(el.charAt(i));
        }
        return pkg.toString();
    }

    private String at(String el) {
        return el.substring(0, el.indexOf('('));
    }

    private String method(String el) {
        int s = el.indexOf('(');
        int e = el.indexOf(')');
        String m = el.substring(s + 1, e);
        if (m.indexOf(':') >= 0) {
            return m.substring(0, m.indexOf(':'));
        }
        return m;
    }

    private int methodLinenum(String el) {
        int s = el.indexOf('(');
        int e = el.indexOf(')');
        String m = el.substring(s + 1, e);
        if (m.indexOf(':') >= 0) {
            return Integer.valueOf(m.substring(m.indexOf(':') + 1).trim());
        }
        return -1;
    }

}
