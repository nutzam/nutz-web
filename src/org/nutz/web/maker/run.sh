#!/bin/bash
export JAVA_OPTS="-Xmx256m -Djava.awt.headless=true"
export APP_LAUNCHER=${pkg}.${pnm}Launcher
export APP_HOME=${phome}
export APP_CONF=$APP_HOME/conf
export APP_LIB=$APP_HOME/lib

# 把各种jar,配置文件啥的加到classpath中吧
cp=.:$JAVA_HOME/lib/rt.jar
cp=$cp:$APP_CONF

cd $APP_LIB
for i in `ls | grep ".jar"`
do
    if [ -f $i ] ; then
        cp=$cp:$PWD/$i
    fi
done

export CLASSPATH=$cp

echo $cp

# 运行吧, 把log打出
nohup java $JAVA_OPTS $APP_LAUNCHER  >> $APP_HOME/server.log 2>&1