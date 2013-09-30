package org.nutz.web.maker;

import java.io.File;

import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 在给定的路径下生产符合nutz-web应用的目录结构与部分文件
 * 
 * @author pw
 * 
 */
public class Maker {

    private static Log log = Logs.get();

    private Maker() {}

    /**
     * 在制定目录生成一个符合nutz-web项目的web工程
     * 
     * @param pc
     */
    public static void newProject(ProjectConf pc) {
        // 打印信息
        log.info("project config :");
        log.info("\n" + Json.toJson(pc));
        // 生成对应目录
        File root = new File(Disks.absolute(pc.pdir));
        Files.createDirIfNoExists(root);
        mkDir(root, "src");
        mkDir(root, "src/" + pc.pkg);
        mkDir(root, "src/" + pc.pkg + "/module");
        mkDir(root, "conf");
        mkDir(root, "test");
        mkDir(root, "test/" + pc.pkg);
        mkDir(root, "ROOT");
        mkDir(root, "ROOT/WEB-INF");
        // 生成对应文件

        // 结束
        log.info("project has been created");
    }

    public static void mkDir(File root, String path) {
        if (-1 != path.indexOf(".")) {
            path = path.replaceAll("\\.", "/");
        }
        String dir = root.getAbsolutePath() + "/" + path;
        log.info("mkdir " + dir);
        Files.createDirIfNoExists(dir);
    }

    public static void main(String[] args) {
        Maker.newProject(ProjectConf.New()
                                    .dir("/Users/pw/workspace2/nutz-web-test/")
                                    .pkg("org.nutz.web.test")
                                    .pnm("WebTest")
                                    .appPort(12345)
                                    .appAdminPort(12346));
    }
}
