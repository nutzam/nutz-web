nutz-web
========

A Jetty launcher + Nutz.Mvc Ajax View

[![Build Status](https://travis-ci.org/nutzam/nutz-web.png?branch=master)](https://travis-ci.org/nutzam/nutz-web)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-web/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-web/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

## 添加依赖(maven)


```xml

		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>nutz-web</artifactId>
			<version>1.r.60</version>
		</dependency>
```

## 新建一个配置文件web.properties

```ini
app-root=src/main/webapp
app-port=8080
```

## 启动

方式一, 直接使用org.nutz.web.WebLauncher,该类自带main方法

方式二,新建一个类,调用org.nutz.web.WebLauncher

```java
package net.wendal.nutzbook;

import org.nutz.web.WebLauncher;

public class MainLauncher extends WebLauncher {

    public static void main(String[] args) {
        WebLauncher.main(args);
    }
}
```

## 将war转为runnable war

首先,打包一个带依赖的nutz-web

```
mvn -Dmaven.test.skip=true clean compile assembly:single -U
```

转换war文件

```
java -jar target\nutz-web-1.r.61-SNAPSHOT-jar-with-dependencies.jar -inject nutzbook-2.9.5.war -output nutzcn.war
```

inject与output不可以相同.