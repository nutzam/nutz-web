package org.nutz.web.maker;

public class ProjectConf {

    public String pdir;

    public String pkg;

    public String pnm;

    public int app_port = 8080;

    public int app_admin_port = 8081;

    public static ProjectConf New() {
        return new ProjectConf();
    }

    public ProjectConf dir(String pdir) {
        this.pdir = pdir;
        return this;
    }

    public ProjectConf pkg(String pkg) {
        this.pkg = pkg;
        return this;
    }

    public ProjectConf appPort(int port) {
        this.app_port = port;
        return this;
    }

    public ProjectConf appAdminPort(int port) {
        this.app_admin_port = port;
        return this;
    }

    public ProjectConf pnm(String pnm) {
        this.pnm = pnm;
        return this;
    }
}
