FROM maven:3.2-jdk-7

MAINTAINER wendal "wendal1985@gmail.com"

ENV NUTZWEB_HOME /usr/share/nutz-web
ENV NUTZWEB_LIBS $NUTZWEB_HOME/libs
ENV NUTZWEB_RS $NUTZWEB_HOME/rs
ENV NUTZWEB_ROOT $NUTZWEB_HOME/Root
ENV NUTZWEB_CLASSES $NUTZWEB_HOME/classes
ENV NUTZWEB_CONF $NUTZWEB_HOME/conf
ENV NUTZWEB_ETC /etc/nutz-web
ENV NUTZWEB_DATA /var/lib/nutz-web
ENV NUTZWEB_PROJECT /var/lib/nutz-web-project
ENV NUTZWEB_LOGS /var/log/nutz-web/
ENV NUTZWEB_MAIN_CLASS org.nutz.web.WebLauncher
ENV NUTZWEB_JAVA_OPTS "-Xmx1g"

RUN mkdir -p $NUTZWEB_HOME $NUTZWEB_LIBS $NUTZWEB_RS $NUTZWEB_ROOT/WEB-INF/ $NUTZWEB_CLASSES $NUTZWEB_CONF $NUTZWEB_ETC $NUTZWEB_DATA $NUTZWEB_PROJECT $NUTZWEB_LOGS

RUN apt-get update 
RUN apt-get install -y --force-yes git

WORKDIR $NUTZWEB_HOME

RUN cd $NUTZWEB_HOME && git clone --depth=1 https://github.com/nutzam/nutz.git  && \
	cd $NUTZWEB_HOME/nutz && mvn -Dmaven.test.skip=true clean package install && cd $NUTZWEB_HOME && rm -fr nutz

# 添加nutz-web的编译
RUN cd $NUTZWEB_HOME && git clone --depth=1 https://github.com/nutzam/nutz-web.git && \
	sed -i 's/1.b.51/1.b.52/g' nutz-web/pom.xml && \
	cd $NUTZWEB_HOME/nutz-web && mvn -Dmaven.test.skip=true clean package install dependency:copy-dependencies && \
	cp $NUTZWEB_HOME/nutz-web/target/dependency/*.jar $NUTZWEB_LIBS/
	cd $NUTZWEB_HOME && rm -fr nutz-web
