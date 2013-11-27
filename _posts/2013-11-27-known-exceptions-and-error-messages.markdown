---
layout: post
title:  "Known exceptions and error messages"
date:   2013-11-27 10:00:00
categories: faq
---

## Bad credential

**Error:**

{% highlight xml %}
  
<entry key="Exception">com.jaeksoft.searchlib.web.ServletException: com.jaeksoft.searchlib.SearchLibException: Bad credential</entry>
 
{% endhighlight %}

This exception occurs when privileges is created but not used while accessing through API.
When at least one user is created in OpenSearchServer every API call must contain some `login` and `key` information.

**Example:**

http://localhost:8080/select?user=index1&login=admin&key=54a51ee4f27cbbcb7a771352b980567f


See also: [https://github.com/jaeksoft/opensearchserver/wiki/Authentication](https://github.com/jaeksoft/opensearchserver/wiki/Authentication)

## Expected to get a directory path

**Error:**

{% highlight xml %}
 
<entry key="Exception">
com.jaeksoft.searchlib.web.ServletException: com.jaeksoft.searchlib.SearchLibException: Expected to get a directory path
</entry>
 
{% endhighlight %}

This exception occurs when the index is not found. Check is the index can be found in the indexes list.
 
**Example:**

http://localhost:8080/select?user=index1 

Index `index1` should be present in the list of indexes.
 
## PHP Library: OSS_API won't work whitout curl extension

**Error :**  

{% highlight xml %}
 
Fatal error: OSS_API won't work whitout curl extension in OSS_API.class.php on line 23
 
{% endhighlight %}

The OpenSearchServer PHP client needs the `php-curl` extension. Install and enable `php-curl` extension and restart your web-server.