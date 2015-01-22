FROM maven:3.2-jdk-8

MAINTAINER wendal "wendal1985@gmail.com"

RUN apt-get update 
RUN apt-get install -y --force-yes zip git wget curl

ENV NUTZWEB_HOME /usr/share/nutzweb
RUN mkdir $NUTZWEB_HOME $NUTZWEB_HOME/bin $NUTZWEB_HOME/libs $NUTZWEB_HOME/rs $NUTZWEB_HOME/ROOT $NUTZWEB_HOME/conf $NUTZWEB_HOME/props
WORKDIR $NUTZWEB_HOME

RUN cd $NUTZWEB_HOME && git clone --depth=1 https://github.com/nutzam/nutz.git  && \
	cd $NUTZWEB_HOME/nutz && mvn -Dmaven.test.skip=true clean package install && cd $NUTZWEB_HOME && rm -fr nutz

# 添加nutz-web的编译
RUN cd $NUTZWEB_HOME && git clone --depth=1 https://github.com/nutzam/nutz-web.git && \
	sed -i 's/1.b.51/1.b.52/g' nutz-web-master/pom.xml && \
	cd $NUTZWEB_HOME/nutz-web && mvn -Dmaven.test.skip=true clean package install dependency:copy-dependencies && \
	cd $NUTZWEB_HOME && rm -fr nutz-web

EXPOSE 8080