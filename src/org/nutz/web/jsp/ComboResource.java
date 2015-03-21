package org.nutz.web.jsp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jetty.util.resource.Resource;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class ComboResource extends Resource {

    private List<Resource> list;

    public ComboResource(Resource... rs) {
        this.list = new LinkedList<Resource>();
        this.addResource(rs);
    }

    public ComboResource addResource(Resource... rs) {
        for (Resource r : rs)
            list.add(r);
        return this;
    }

    public ComboResource clear() {
        list.clear();
        return this;
    }

    public Resource addPath(String path) throws IOException, MalformedURLException {
        for (Resource res : list) {
            Resource r = res.addPath(path);
            if (r.exists())
                return r;
        }
        throw Lang.makeThrow("Resource noexists : '%s'", path);
    }

    public boolean isContainedIn(Resource r) throws MalformedURLException {
        for (Resource res : list) {
            if (res.isContainedIn(r))
                return true;
        }
        return false;
    }

    public void close() {
        for (Resource res : list) {
            try {
                Mirror.me(res).invoke(res, "close");
            }
            catch (Exception e) {}
        }
    }

    public void release() {
        for (Resource res : list) {
            try {
                Mirror.me(res).invoke(res, "release");
            }
            catch (Exception e) {}
        }

    }

    public boolean exists() {
        for (Resource res : list) {
            if (res.exists())
                return true;
        }
        return false;
    }

    public boolean isDirectory() {
        for (Resource res : list) {
            if (!res.isDirectory())
                return false;
        }
        return true;
    }

    public long lastModified() {
        throw Lang.noImplement();
    }

    public long length() {
        throw Lang.noImplement();
    }

    public URL getURL() {
        throw Lang.noImplement();
    }

    public File getFile() throws IOException {
        throw Lang.noImplement();
    }

    public String getName() {
        throw Lang.noImplement();
    }

    public InputStream getInputStream() throws IOException {
        throw Lang.noImplement();
    }

    public boolean delete() throws SecurityException {
        throw Lang.noImplement();
    }

    public boolean renameTo(Resource dest) throws SecurityException {
        throw Lang.noImplement();
    }

    public String[] list() {
        throw Lang.noImplement();
    }

    public ReadableByteChannel getReadableByteChannel() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public OutputStream getOutputStream() throws IOException, SecurityException {
        throw Lang.noImplement();
    }

}
