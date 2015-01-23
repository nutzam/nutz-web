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
        shell("git clone --depth=1 " + git_repo)
        repo = tmp_dir + "/" + os.listdir(tmp_dir)[0]
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
        #subprocess.call(["pwd"])
        #print cmd
        shell(cmd)
        
    #编译src里面的文件
    cp = _cp()
    pom_path = repo + "/pom.xml"
    if os.path.exists(pom_path) :
        os.chdir(repo)
        shell("mvn -Dmaven.test.skip=true clean package install dependency:copy-dependencies ")
        shell("cp target/*.jar " + os.environ.get("NUTZWEB_LIBS"))
        shell("cp target/dependency/* " + os.environ.get("NUTZWEB_LIBS"))
    else :
        print "Warnning", "without pom.xml"
        
        
    os.chdir("/")
    subprocess.call(["rm", "-fr", tmp_dir])
    
def shell(cmd):
    print cmd
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