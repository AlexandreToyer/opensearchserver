---
layout: post
title:  "How to check the environment variables on Windows"
date:   2013-11-27 10:00:00
categories: faq
---

1. Right click **My Computer**.
2. Select **Properties**.
3. Select **Advanced tab**.
4. Select **Environment Variables**.

You will see a list of variables. What you need is either a `JRE_HOME` or `JAVA_HOME` variable, depending on the type of Java engine you are running.

If the correct variable is present, please check that it targets the correct folder.

If the desired variable is not present, create it like this:

* If you have a JRE, create or update the environment variable `JRE_HOME`. Set it to target the folder where your Java JRE is, for example `C:\Program Files\Java\jre1.6.0_14`.
* If you have a JDK, create or update the environment variable `JAVA_HOME`. Set it to target the folder where your Java JDK is, for example `C:\Program Files\Java\jdk1.6.0_14`.

Once you have done that, try to launch OpenSearchServer again with `start.bat`.

If you have Java but you do not see the variable and do you not remember whether you have a JDK or JRE, go to the Start menu, Click Run, type cmd to call up the command line interface, and type java -version to see what's installed. Checking whether your 8080 port is free

If, by chance, you already have a server listening on the 8080 tcp port you'll have to change it in the Catalina config files.

To check whether the 8080 port is already occupied use the following command line: `netstat -o -n -a`

If you have a response like the one below, your port is not free:

{% highlight bash %}

TCP 0.0.0.0:8080 0.0.0.0:0 LISTENING 676

{% endhighlight %}


If port 8080 is already being used, we suggest that you simply use another port for ISS. To do so:

In your OpenSearchServer folder, open the file apache-tomcat-6.0.20/conf/server.xml Locate the line starting with: `<Connector port="8080" ...` and then change it to `<connector port="8081" ...`

Save your file and restart OpenSearchServer with start.bat. Your OpenSearchServer Back Office is now available at this address: http://localhost:8081