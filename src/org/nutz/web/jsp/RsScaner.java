package org.nutz.web.jsp;

import java.io.File;
import java.util.HashMap;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.Disks;
import org.nutz.web.Webs;

/**
 * 根据配置信息，生成一系列 SCRIPT 和 LINK 的标签
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class RsScaner {

    /**
     * 本类是否是强制模式，如果不是，那么整个应用生命周期仅扫描一次
     */
    private boolean force;

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    private String _links;

    public String render() {
        if (force || null == _links) {
            _links = doScan();
        }
        return _links;
    }

    private String doScan() {
        final StringBuilder sb = new StringBuilder();
        final HashMap<String, Boolean> paths = new HashMap<String, Boolean>();
        File home = Files.createDirIfNoExists(rsHome);
        Context context = Lang.context();
        context.set(Webs.RS, rs);

        for (String path : scanPaths) {
            // 分析字符串
            int pos = path.indexOf(':');
            String[] incld = null;
            String[] excld = null;
            if (pos > 0) {
                String str = path.substring(pos + 1);
                if (str.startsWith("!")) {
                    excld = Strings.splitIgnoreBlank(str.substring(1));

                } else {
                    incld = Strings.splitIgnoreBlank(str);
                }
                path = path.substring(0, pos);
            }

            // 搜索一下，看看资源的磁盘路径在哪里
            File theF = Files.getFile(home, path);
            // 文件
            if (theF.isFile()) {
                _do_append(sb, paths, home, context, theF, incld, excld);
                continue;
            }
            // 目录
            else if (theF.isDirectory()) {
                File[] files = theF.listFiles();
                for (File f : files)
                    if (f.isFile())
                        _do_append(sb, paths, home, context, f, incld, excld);
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    private boolean _contains_(String str, String[] subs) {
        for (String sub : subs)
            if (str.contains(sub))
                return true;
        return false;
    }

    private void _do_append(final StringBuilder sb,
                            final HashMap<String, Boolean> paths,
                            File home,
                            Context context,
                            File theF,
                            String[] incld,
                            String[] excld) {
        String p = Disks.getRelativePath(home, theF);
        if (null != incld && !_contains_(p, incld))
            return;
        if (null != excld && _contains_(p, excld))
            return;

        if (!paths.containsKey(p)) {
            paths.put(p, true);
            if (p.endsWith(".css")) {
                sb.append(segCss.render(context.set("path", p)));
            } else if (p.endsWith(".js")) {
                sb.append(segJs.render(context.set("path", p)));
            }
        }
        sb.append('\n');
    }

    private String rs;

    private Segment segJs;

    private Segment segCss;

    private String rsHome;

    private String[] scanPaths;

    public String getRs() {
        return rs;
    }

    public void setRs(String rs) {
        this.rs = rs;
    }

    public Segment getSegJs() {
        return segJs;
    }

    public void setSegJs(Segment segJs) {
        this.segJs = segJs;
    }

    public Segment getSegCss() {
        return segCss;
    }

    public void setSegCss(Segment segCss) {
        this.segCss = segCss;
    }

    public String getRsHome() {
        return rsHome;
    }

    public void setRsHome(String rsHome) {
        this.rsHome = rsHome;
    }

    public String[] getScanPaths() {
        return scanPaths;
    }

    public void setScanPaths(String[] scanPaths) {
        this.scanPaths = scanPaths;
    }

}
