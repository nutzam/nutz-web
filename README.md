nutz-web
========

A Jetty launcher + Nutz.Mvc Ajax View

[![Build Status](https://travis-ci.org/nutzam/nutz-web.png?branch=master)](https://travis-ci.org/nutzam/nutz)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-web/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

## 添加依赖(maven)

```xml

		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>nutz-web</artifactId>
			<version>1.r.57-SNAPSHOT</version>
			<exclusions> <!-- nutz-web为了兼容Android所以默认使用jetty 7.x, 这里我们换成最新的jetty9 -->
				<exclusion>
					<groupId>org.eclipse.jetty.aggregate</groupId>
					<artifactId>jetty-all-server</artifactId>
				</exclusion>
				<exclusion>
					<groupId>org.eclipse.jetty.orbit</groupId>
					<artifactId>javax.servlet.jsp</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		
		<dependency>
			<groupId>org.eclipse.jetty</groupId>
			<artifactId>jetty-servlets</artifactId>
			<version>9.2.17.v20160517</version>
		</dependency>
		<dependency>
			<groupId>org.eclipse.jetty.websocket</groupId>
			<artifactId>websocket-server</artifactId>
			<version>9.2.17.v20160517</version>
		</dependency>
		<dependency>
			<groupId>org.eclipse.jetty.websocket</groupId>
			<artifactId>websocket-client</artifactId>
			<version>9.2.17.v20160517</version>
		</dependency>
		<dependency>
			<groupId>javax.websocket</groupId>
			<artifactId>javax.websocket-api</artifactId>
			<version>1.1</version>
		</dependency>
		<dependency>
			<groupId>org.eclipse.jetty.websocket</groupId>
			<artifactId>javax-websocket-server-impl</artifactId>
			<version>9.2.17.v20160517</version>
		</dependency>
```

## 新建一个配置文件web.properties

```ini
app-root=src/main/webapp
app-port=8080
```