package org.nutz.web.god;

import java.io.File;
import java.io.IOException;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;

public class GodKey {

	public static final String ATTR_NAME = "nutz.web.god_key";

	private static final String GOD_KEY_FILE = "~/.nutz_web_god_key";

	private String key;

	public GodKey() {
		reset();
	}

	public boolean match(String key) {
		if (null == this.key || null == key)
			return false;
		return this.key.equals(key);
	}

	public String getKey() {
		return key;
	}

	public void clear() {
		key = null;
		File f = Files.findFile(GOD_KEY_FILE);
		if (null != f)
			Files.deleteFile(f);
	}

	public void reset() {
		try {
			File f = Files.createFileIfNoExists(GOD_KEY_FILE);
			key = R.UU64();
			Files.write(f, key + "\r\n");
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
