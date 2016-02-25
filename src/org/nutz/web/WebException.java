package org.nutz.web;

import org.nutz.json.Json;
import org.nutz.lang.Strings;

public class WebException extends RuntimeException {

    private static final long serialVersionUID = 3343036182101828118L;

    private String key;

    private Object reason;

    public WebException() {
        super();
    }

    public WebException(Throwable cause) {
        super(cause);
    }

    public String getKey() {
        return key;
    }

    public boolean isKey(String key) {
        return null != key && null != this.key && key.equals(this.key);
    }

    public WebException key(String key) {
        this.key = key;
        return this;
    }

    public Object getReason() {
        return reason;
    }

    public String getReasonString() {
        return Strings.sNull(reason, "");
    }

    public WebException reasonf(String fmt, Object... args) {
        this.reason = String.format(fmt, args);
        return this;
    }

    public WebException reason(Object msg) {
        this.reason = null == msg ? null : msg;
        return this;
    }

    public String toString() {
        if (null == reason) {
            return key;
        }
        if (reason instanceof String[]) {
        	return key + ":" + Json.toJson(reason);
        }
        return key + " : " + reason;
    }

}
