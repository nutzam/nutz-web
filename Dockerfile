FROM dockerfile/java:oracle-java8

MAINTAINER wendal "wendal1985@gmail.com"

# add maven

ENV MAVEN_VERSION 3.1.1

RUN curl -sSL http://mirror.bit.edu.cn/apache/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

# add tomcat
#ENV CATALINA_HOME /usr/local/tomcat
#ENV PATH $CATALINA_HOME/bin:$PATH
#RUN mkdir -p "$CATALINA_HOME"
#WORKDIR $CATALINA_HOME

# see https://www.apache.org/dist/tomcat/tomcat-8/KEYS
# RUN gpg --keyserver pool.sks-keyservers.net --recv-keys \
# 	05AB33110949707C93A279E3D3EFE6B686867BA6 \
# 	07E48665A34DCAFAE522E5E6266191C37C037D42 \
# 	47309207D818FFD8DCD3F83F1931D684307A10A5 \
# 	541FBE7D8F78B25E055DDEE13C370389288584E7 \
# 	61B832AC2F1C5A90F0F9B00A1C506407564C17A3 \
# 	713DA88BE50911535FE716F5208B0AB1D63011C7 \
# 	79F7026C690BAA50B92CD8B66A3AD3F4F22C4FED \
# 	9BA44C2621385CB966EBA586F72C284D731FABEE \
# 	A27677289986DB50844682F8ACB77FC2E86E29AC \
# 	A9C5DF4D22E99998D9875A5110C01C5A2F6059E7 \
# 	DCFD35E0BF8CA7344752DE8B6FB21E8933C60243 \
# 	F3A04C595DB5B6A5F1ECA43E3B7BBB100D811BBE \
# 	F7DA48BB64BCB84ECBA7EE6935CD23C10D498E23
# 
# ENV TOMCAT_MAJOR 7
# ENV TOMCAT_VERSION 7.0.57
# ENV TOMCAT_TGZ_URL https://www.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz

#RUN curl -SL "$TOMCAT_TGZ_URL" -o tomcat.tar.gz \
#	&& curl -SL "$TOMCAT_TGZ_URL.asc" -o tomcat.tar.gz.asc \
#	&& gpg --verify tomcat.tar.gz.asc \
#	&& tar -xvf tomcat.tar.gz --strip-components=1 \
#	&& rm bin/*.bat \
#	&& rm tomcat.tar.gz* && rm -fr /usr/local/tomcat/webapps/* && \
#	mkdir /usr/local/tomcat/webapps/ROOT
	
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
				  
# 改成oschina的maven库
#RUN wget -O $MAVEN_HOME/conf/settings.xml http://maven.oschina.net/static/xml/settings.xml
  
RUN apt-get update && apt-get install -y --force-yes zip git wget curl

ENV NUTZWEB_HOME /usr/share/nutzweb
RUN mkdir $NUTZWEB_HOME $NUTZWEB_HOME/bin $NUTZWEB_HOME/libs $NUTZWEB_HOME/rs $NUTZWEB_HOME/ROOT $NUTZWEB_HOME/conf $NUTZWEB_HOME/props
WORKDIR $NUTZWEB_HOME

RUN cd $NUTZWEB_HOME && wget https://github.com/nutzam/nutz/archive/master.zip && unzip master.zip && rm -fr master.zip && \
	cd $NUTZWEB_HOME/nutz-master && mvn dependency:copy-dependencies && \
	cd $NUTZWEB_HOME/nutz-master && mvn -Dmaven.test.skip=true clean package install && cp target/dependency/* $NUTZWEB_HOME/libs/ && \
	cp $NUTZWEB_HOME/nutz-master/nutz-1.b.52.jar $NUTZWEB_HOME/libs/ && \
	cd $NUTZWEB_HOME && rm -fr $NUTZWEB_HOME/nutz-master

# 添加nutz-web的编译
RUN cd $NUTZWEB_HOME && wget https://github.com/nutzam/nutz-web/archive/master.zip && unzip master.zip &&\
	rm -fr master.zip && date && sed -i 's/1.b.51/1.b.52/g' nutz-web-master/pom.xml && \
	cd $NUTZWEB_HOME/nutz-web-master && mvn dependency:copy-dependencies && \
	cd $NUTZWEB_HOME/nutz-web-master && mvn -Dmaven.test.skip=true clean package install && cp target/dependency/* $NUTZWEB_HOME/libs/ && \
	cp $NUTZWEB_HOME/nutz-master/nutz-web-1.b.52.jar $NUTZWEB_HOME/libs/ && \
	cd $NUTZWEB_HOME && rm -fr $NUTZWEB_HOME/nutz-web-master


EXPOSE 8080