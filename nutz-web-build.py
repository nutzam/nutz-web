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
    if os.path.exists(tmp_dir) :
        subprocess.call(["rm", "-fr", tmp_dir])
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
    
    #看看有无ROOT,rs,conf, 一一拷贝过去
    os.chdir(repo)
    for nm in ["ROOT", "rs", "conf", "libs"] :
        p = repo + "/" + nm
        if not os.path.exists(p) :
            continue
        cmd = "cp -fr %s/* %s/%s/" % (p, os.environ.get("NUTZWEB_HOME"), nm)
        subprocess.call(["pwd"])
        print cmd
        shell(cmd)
        
    #编译src里面的文件
    cp = _cp()
    src_dir = repo + "/src"
    if os.path.exists(src_dir) :
        os.mkdir(tmp_dir + "/classes")
        os.system("shopt -s globstar")
        cmd = "javac -g -cp %s -d %s/classes -encoding utf8 src/**/*.java" % (cp, tmp_dir)
        print "invoke javac:", cmd
        with open("/tmp/javac.sh", "w") as f :
            f.write("#!/bin/bash\n")
            f.write("shopt -s globstar\n")
            f.write(cmd)
        shell("chmod 777 /tmp/javac.sh")
        shell("bash /tmp/javac.sh")
        shell("cp -fr %s/classes/* %s/" % (tmp_dir, os.environ.get("NUTZWEB_CLASSES")))
        shell("cp -fr %s/* %s" % (src_dir, os.environ.get("NUTZWEB_CLASSES")))
        shell("""find %s -name "*.java" -delete """ % (src_dir))
        subprocess.check_call(["rm", "-fr", "classes"])
    else :
        print "Warnning", "without src dir"
        
        
    os.chdir("/")
    subprocess.call(["rm", "-fr", tmp_dir])
    
def shell(cmd):
    subprocess.check_call(cmd, shell=1)
        
def _cp():
    cp = os.environ.get("NUTZWEB_CONF") + ":" + os.environ.get("NUTZWEB_CLASSES")
    NUTZWEB_LIBS = os.environ.get("NUTZWEB_LIBS")
    for nm in os.listdir(NUTZWEB_LIBS) :
        cp +=  ":" + NUTZWEB_LIBS + "/" + nm
    print "classpath:", cp
    return cp
    

if __name__ == "__main__" :
    main()