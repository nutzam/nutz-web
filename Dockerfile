FROM nutzam/nutz-web:docker-base

MAINTAINER wendal "wendal1985@gmail.com"

ENV ANT_VERSION 1.9.4
RUN curl http://www.us.apache.org/dist/ant/binaries/apache-ant-$ANT_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-ant-$ANT_VERSION /usr/share/ant \
  && ln -s /usr/share/ant/bin/ant /usr/bin/ant

# 定义入口
VOLUME ["/etc/nutz-web", "/var/lib/nutz-web", "/var/lib/nutz-web-project", "/var/log/nutz-web"]
EXPOSE 8080