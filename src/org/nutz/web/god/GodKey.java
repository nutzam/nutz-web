package org.nutz.web.god;

import java.io.File;
import java.io.IOException;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;

public class GodKey {

    public static final String ATTR_NAME = "nutz.web.god_key";

    private static final String GOD_KEY_FILE = "~/.nutz_web_god_key";

    private String god_key_file_my;

    // 一般应用使用一个超级用户（上帝）
    private String key;

    // 超级用户 亚当，权限与上帝一致，可以查看，修改
    private String adam;

    // 次超级用户 夏娃，权限是可以查看
    private String eva;

    /**
     * 在当前用户目录下，生成.nutz_web_god_key文件。
     * 
     * 注意，如果一个服务器上有多个应用，请指定god_key文件。
     * 
     */
    public GodKey() {
        god_key_file_my = GOD_KEY_FILE;
        reset();
    }

    /**
     * 在指定的文件中生成god_key，推荐使用。
     * 
     * @param keyFile
     */
    public GodKey(String keyFile) {
        god_key_file_my = Strings.isBlank(keyFile) ? GOD_KEY_FILE : keyFile;
        reset();
    }

    public boolean match(String key) {
        return keyMatch(this.key, key);
    }

    public String getKey() {
        return key;
    }

    public boolean isAdam(String key) {
        return keyMatch(this.adam, key);
    }

    public boolean isEve(String key) {
        return keyMatch(this.eva, key);
    }

    private boolean keyMatch(String tKey, String yKey) {
        if (null == tKey || null == yKey)
            return false;
        return tKey.equals(yKey);
    }

    public void clear() {
        key = null;
        File f = Files.findFile(god_key_file_my);
        if (null != f)
            Files.deleteFile(f);
    }

    public void reset() {
        try {
            File f = Files.createFileIfNoExists(god_key_file_my);
            key = R.UU64();
            Files.write(f, key + "\r\n");
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
