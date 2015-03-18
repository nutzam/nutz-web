package org.nutz.web.jsp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jetty.util.resource.Resource;
import org.nutz.lang.Lang;

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

    @Override
    public Resource addPath(String path) throws IOException, MalformedURLException {
        for (Resource res : list) {
            Resource r = res.addPath(path);
            if (r.exists())
                return r;
        }
        throw Lang.makeThrow("Resource noexists : '%s'", path);
    }

    @Override
    public boolean isContainedIn(Resource r) throws MalformedURLException {
        for (Resource res : list) {
            if (res.isContainedIn(r))
                return true;
        }
        return false;
    }

    @Override
    public void release() {
        for (Resource res : list) {
            res.release();
        }
    }

    @Override
    public boolean exists() {
        for (Resource res : list) {
            if (res.exists())
                return true;
        }
        return false;
    }

    @Override
    public boolean isDirectory() {
        for (Resource res : list) {
            if (!res.isDirectory())
                return false;
        }
        return true;
    }

    @Override
    public long lastModified() {
        throw Lang.noImplement();
    }

    @Override
    public long length() {
        throw Lang.noImplement();
    }

    @Override
    public URL getURL() {
        throw Lang.noImplement();
    }

    @Override
    public File getFile() throws IOException {
        throw Lang.noImplement();
    }

    @Override
    public String getName() {
        throw Lang.noImplement();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw Lang.noImplement();
    }

    @Override
    public OutputStream getOutputStream() throws IOException, SecurityException {
        throw Lang.noImplement();
    }

    @Override
    public boolean delete() throws SecurityException {
        throw Lang.noImplement();
    }

    @Override
    public boolean renameTo(Resource dest) throws SecurityException {
        throw Lang.noImplement();
    }

    @Override
    public String[] list() {
        throw Lang.noImplement();
    }

}
