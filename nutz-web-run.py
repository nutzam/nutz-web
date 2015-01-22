#!/usr/bin/python
# -*- coding: UTF-8 -*-
from symbol import try_stmt
import subprocess
import os

def main():
    for k,v in os.environ.iteritems() :
        if str(k).startswith("NUTZWEB") :
            print ("%-20s: %s" % (str(k), str(v)))
    
    print("------------------------------------------------")
    cp = os.environ.get("NUTZWEB_ETC") + ":" + os.environ.get("key")
    NUTZWEB_LIBS = os.environ.get("NUTZWEB_LIBS")
    for nm in os.listdir(NUTZWEB_LIBS) :
        cp +=  ":" + NUTZWEB_LIBS + "/" + nm
    print "classpath:", cp
    MAIN_CLASS = os.environ.get("NUTZWEB_MAIN_CLASS")
    cmd = "nohup java -cp %s %s %s" % (cp, os.environ.get("NUTZWEB_JAVA_OPTS", ""), MAIN_CLASS)
    os.system(cmd)

if __name__ == "__main__" :
    main()