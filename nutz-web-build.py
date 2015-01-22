#!/usr/bin/python
# -*- coding: UTF-8 -*-
from symbol import try_stmt
import subprocess
import os
import sys

def main():
    if len(sys.argv) == 1 :
        return
    repo = str(sys.argv[1])
    tmp_dir = "/tmp/nutz-web-build"
    os.mkdir(tmp_dir)
    os.chdir(tmp_dir)
    git_repo = None
    if repo.startswith("git@") or repo.startswith("https:") :
        git_repo = repo
        subprocess.check_call("git clone --depth=1 " + git_repo)
        repo = tmp_dir + "/" + os.listdir(path)[0]
    else :
        if not os.path.exists(repo) :
            print "not such dir", repo
            sys.exit(1)
            return
        if not os.path.isdir(repo) :
            print "not dir", repo
            sys.exit(1)
            return
        if not os.listdir(repo) :
            print "emtry dir", repo
            sys.exit(1)
            return
    #先编译src里面的文件
    cp = _cp()
    src_dir = repo + "/src"
    if os.path.exists(src_dir) :
        os.mkdir("classes")
        cmd = "javac -g -cp %s -s %s -d classes" % (cp, src_dir)
        print "invoke javac:", cmd
        subprocess.check_call(cmd)
        subprocess.check_call("cp -fr classes/* " + os.environ.get("NUTZWEB_CLASSES") + "/")
        subprocess.check_call(["rm", "-fr", "classes"])
    else :
        print "Warnning", "without src dir"
        
    #看看有无ROOT,rs,conf, 一一拷贝过去
    for nm in ["ROOT", "rs", "conf", "libs"] :
        p = repo + "/" + nm
        if not os.path.exists(p) :
            continue
        subprocess.call("cp -fr %s/* %s/%s/" % (p, os.environ.get("NUTZWEB_HOME"), p))
        
    os.chdir("/")
    subprocess.call(["rm", "-fr", tmp_dir])
        
def _cp():
    cp = os.environ.get("NUTZWEB_CONF") + ":" + os.environ.get("NUTZWEB_CLASSES")
    NUTZWEB_LIBS = os.environ.get("NUTZWEB_LIBS")
    for nm in os.listdir(NUTZWEB_LIBS) :
        cp +=  ":" + NUTZWEB_LIBS + "/" + nm
    print "classpath:", cp
    return cp
    

if __name__ == "__main__" :
    main()