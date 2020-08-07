Typical spring boot application which has spring-boot-starter-web in their dependency, uses logback implementation for logging. Projects can override this by excluding spring-boot-starter-logging and choose log4j2 implementation by adding spring-boot-starter-log4j2. This solution will focus on these two logging implementation and solves the issue of handling multi-line logs.

 


Problem
With the current logging setup, (More info can be found Centralized logging with kuberenetes . TLDR; applications write logs to stdout, docker then streams this through the configured logging driver and writes these logs to files on the EC2 nodes where the application(pod) is running, filebeat running as daemon on every node reads these files and pushes to fluentd, fluentd aggregates/filters logs and sends them to Corralogix) we use the json-file logging driver of docker. Note that this can't be changed per application. This is configured for an entire cluster. Docker CE provides docker logs support for only json-file, local and journald drivers, so only these driver's logs can be viewed from Kubernetes dashboard and while doing kubectl logs <pod>

 

When the application logs, for example

2020-05-07 07:35:14.009  INFO [nio-8080-exec-1] c.h.u.c.UserController                   : Received request to raise exception:
2020-05-07 07:35:14.009 ERROR [nio-8080-exec-1] c.h.u.c.UserController                   : error:
java.lang.Exception: Raising an exception
	at com.hotstar.usermanagement.controller.UserController.raiseException(UserController.java:106)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190)
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138)
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:106)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:888)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:793)
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1040)
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943)
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898)
 

This whole message itself is broken into several log events by the json-file logging driver, which can be seen in the container log file as below

{"log":"2020-05-07 07:35:14.009  INFO [nio-8080-exec-1] c.h.u.c.UserController                   : Received request to raise exception:\n","stream":"stdout","time":"2020-05-07T07:35:14.009713884Z"}
{"log":"2020-05-07 07:35:14.009 ERROR [nio-8080-exec-1] c.h.u.c.UserController                   : error: \n","stream":"stdout","time":"2020-05-07T07:35:14.010118297Z"}
{"log":"java.lang.Exception: Raising an exception\n","stream":"stdout","time":"2020-05-07T07:35:14.01012426Z"}
{"log":"\u0009at com.hotstar.usermanagement.controller.UserController.raiseException(UserController.java:106)\n","stream":"stdout","time":"2020-05-07T07:35:14.010126885Z"}
{"log":"\u0009at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n","stream":"stdout","time":"2020-05-07T07:35:14.010129887Z"}
{"log":"\u0009at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n","stream":"stdout","time":"2020-05-07T07:35:14.010132411Z"}
{"log":"\u0009at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n","stream":"stdout","time":"2020-05-07T07:35:14.010134825Z"}
{"log":"\u0009at java.lang.reflect.Method.invoke(Method.java:498)\n","stream":"stdout","time":"2020-05-07T07:35:14.010137255Z"}
{"log":"\u0009at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190)\n","stream":"stdout","time":"2020-05-07T07:35:14.0101396Z"}
{"log":"\u0009at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138)\n","stream":"stdout","time":"2020-05-07T07:35:14.010142185Z"}
{"log":"\u0009at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:106)\n","stream":"stdout","time":"2020-05-07T07:35:14.01014471Z"}
{"log":"\u0009at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:888)\n","stream":"stdout","time":"2020-05-07T07:35:14.010147397Z"}
{"log":"\u0009at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:793)\n","stream":"stdout","time":"2020-05-07T07:35:14.010150117Z"}
{"log":"\u0009at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\n","stream":"stdout","time":"2020-05-07T07:35:14.010152717Z"}
{"log":"\u0009at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1040)\n","stream":"stdout","time":"2020-05-07T07:35:14.010155222Z"}
{"log":"\u0009at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943)\n","stream":"stdout","time":"2020-05-07T07:35:14.010157629Z"}
{"log":"\u0009at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)\n","stream":"stdout","time":"2020-05-07T07:35:14.010160192Z"}
{"log":"\u0009at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898)\n","stream":"stdout","time":"2020-05-07T07:35:14.010162607Z"}
{"log":"\u0009at javax.servlet.http.HttpServlet.service(HttpServlet.java:634)\n","stream":"stdout","time":"2020-05-07T07:35:14.010165013Z"}
{"log":"\u0009at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)\n","stream":"stdout","time":"2020-05-07T07:35:14.010167368Z"}
{"log":"\u0009at javax.servlet.http.HttpServlet.service(HttpServlet.java:741)\n","stream":"stdout","time":"2020-05-07T07:35:14.0101698Z"}
{"log":"\u0009at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)\n","stream":"stdout","time":"2020-05-07T07:35:14.010172177Z"}
{"log":"\u0009at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n","stream":"stdout","time":"2020-05-07T07:35:14.010174919Z"}
{"log":"\u0009at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)\n","stream":"stdout","time":"2020-05-07T07:35:14.010177437Z"}
{"log":"\u0009at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n","stream":"stdout","time":"2020-05-07T07:35:14.010179835Z"}
{"log":"\u0009at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n","stream":"stdout","time":"2020-05-07T07:35:14.010182322Z"}
{"log":"\u0009at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\n","stream":"stdout","time":"2020-05-07T07:35:14.010188194Z"}
{"log":"\u0009at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\n","stream":"stdout","time":"2020-05-07T07:35:14.010190705Z"}
{"log":"\u0009at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n","stream":"stdout","time":"2020-05-07T07:35:14.010193153Z"}
{"log":"\u0009at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n","stream":"stdout","time":"2020-05-07T07:35:14.010195619Z"}
{"log":"\u0009at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\n","stream":"stdout","time":"2020-05-07T07:35:14.010200049Z"}
{"log":"\u0009at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\n","stream":"stdout","time":"2020-05-07T07:35:14.010203699Z"}
{"log":"\u0009at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n","stream":"stdout","time":"2020-05-07T07:35:14.010207544Z"}
{"log":"\u0009at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n","stream":"stdout","time":"2020-05-07T07:35:14.01021137Z"}
The Docker json logging driver treats each line as a separate message.


Solution
One of the solution is to have the application use a structured logging layout, like JSON. Enabling this depends on the logging implementation selected in your application.


Log4j2
If you are using log4j2, then use JsonLayout  in your log4j2.xml property file.

<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="INFO">
    <Appenders>
        <Console name="ConsoleJSONAppender" target="SYSTEM_OUT">
            <JsonLayout eventEol="true" compact="true" stacktraceAsString="true">
            </JsonLayout>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="${env:LOG_LEVEL:-info}" additivity="false">
            <AppenderRef ref="ConsoleJSONAppender" />
        </Root>
    </Loggers>
</Configuration>
Here, you are configuring the Console logs to use JsonLayout, which is compacted (compact=true) and each log event is broken into multiple log events (eventEol=true). Stacktrace in case of exception will be shown as a string (stacktracAsString=true) instead of object format (which will be huge).



Logback
If you are using logback, then we need to add json encoder and it’s dependencies to the project. Add the following dependencies logback-json-classic , logback-jackson and jackson-databind assuming you already have logback-classic dependency added. Please check the appropriate versions and add accordingly.


		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-classic</artifactId>
			<version>1.2.3</version>
		</dependency>
		<dependency>
			<groupId>ch.qos.logback.contrib</groupId>
			<artifactId>logback-json-classic</artifactId>
			<version>0.1.5</version>
		</dependency>
		<dependency>
			<groupId>ch.qos.logback.contrib</groupId>
			<artifactId>logback-jackson</artifactId>
			<version>0.1.5</version>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-databind</artifactId>
			<version>2.11.0</version>
		</dependency>
Once these dependencies are added, then use the JsonLayout in the appropriate logback configuration. 

    <variable name="LOG_LEVEL" value="${LOG_LEVEL}" />
    <appender name="STDOUT_JSON" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.contrib.json.classic.JsonLayout">
            <jsonFormatter class="ch.qos.logback.contrib.jackson.JacksonJsonFormatter">
               <prettyPrint>false</prettyPrint>
            </jsonFormatter>
            <appendLineSeparator>true</appendLineSeparator>
        </layout>
    </appender>
    <root level="${LOG_LEVEL}">
        <appender-ref ref="STDOUT_JSON"/>
    </root>
Please make sure the prettyPrint is set to false and appendLineSeparator is set to true. This will configure the log events to be compact and adds separate json message for each log event.



 

In both these cases, the logs will be available in the K8s dashboard 



 