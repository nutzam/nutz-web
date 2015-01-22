FROM nutzam/nutz-web:docker-base

MAINTAINER wendal "wendal1985@gmail.com"

# 定义入口
COPY nutz-web-run.py /nutz-web-run.py
CMD python /nutz-web-run.py >> $NUTZWEB_LOGS/main.log
VOLUME ["/etc/nutz-web", "/var/lib/nutz-web", "/var/lib/nutz-web-project"]
EXPOSE 8080