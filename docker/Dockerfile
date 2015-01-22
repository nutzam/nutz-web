FROM maven

MAINTAINER wendal "wendal1985@gmail.com"
	
# 改成163的源
#RUN /bin/echo -e "deb http://mirrors.163.com/ubuntu/ trusty main restricted universe multiverse\n\
#				  deb http://mirrors.163.com/ubuntu/ trusty-security main restricted universe multiverse\n\
#				  deb http://mirrors.163.com/ubuntu/ trusty-updates main restricted universe multiverse\n\
#				  deb http://mirrors.163.com/ubuntu/ trusty-proposed main restricted universe multiverse\n\
#				  deb http://mirrors.163.com/ubuntu/ trusty-backports main restricted universe multiverse\n\
#				  deb-src http://mirrors.163.com/ubuntu/ trusty main restricted universe multiverse\n\
#				  deb-src http://mirrors.163.com/ubuntu/ trusty-security main restricted universe multiverse\n\
#				  deb-src http://mirrors.163.com/ubuntu/ trusty-updates main restricted universe multiverse\n\
#				  deb-src http://mirrors.163.com/ubuntu/ trusty-proposed main restricted universe multiverse\n\
#				  deb-src http://mirrors.163.com/ubuntu/ trusty-backports main restricted universe multiverse" > /etc/apt/sources.list
#				  
## 改成oschina的maven库
##RUN wget -O $MAVEN_HOME/conf/settings.xml http://maven.oschina.net/static/xml/settings.xml
  
RUN apt-get update 
RUN apt-get install -y --force-yes zip git wget curl

ENV NUTZWEB_HOME /usr/share/nutzweb
RUN mkdir $NUTZWEB_HOME $NUTZWEB_HOME/bin $NUTZWEB_HOME/libs $NUTZWEB_HOME/rs $NUTZWEB_HOME/ROOT $NUTZWEB_HOME/conf $NUTZWEB_HOME/props
WORKDIR $NUTZWEB_HOME

RUN cd $NUTZWEB_HOME && wget https://github.com/nutzam/nutz/archive/master.zip && unzip master.zip && rm -fr master.zip && \
	cd $NUTZWEB_HOME/nutz-master && mvn -Dmaven.test.skip=true clean package install

# 添加nutz-web的编译
RUN cd $NUTZWEB_HOME && wget https://github.com/nutzam/nutz-web/archive/master.zip && \
	unzip master.zip && rm -fr master.zip && date && sed -i 's/1.b.51/1.b.52/g' nutz-web-master/pom.xml && \
	cd $NUTZWEB_HOME/nutz-web-master && mvn -Dmaven.test.skip=true clean package install dependency:copy-dependencies


EXPOSE 8080