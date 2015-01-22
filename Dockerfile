FROM nutzam/nutz-web:docker-base

MAINTAINER wendal "wendal1985@gmail.com"

ENV NUTZWEB_DO_BUILD "python /nutz-web-build.py "

# 定义入口
COPY nutz-web-run.py /nutz-web-run.py
COPY nutz-web-build.py /nutz-web-build.py
CMD python /nutz-web-run.py >> $NUTZWEB_LOGS/main.log
VOLUME ["/etc/nutz-web", "/var/lib/nutz-web", "/var/lib/nutz-web-project", "/var/log/nutz-web"]
EXPOSE 8080