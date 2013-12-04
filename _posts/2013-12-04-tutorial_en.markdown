---
layout: post
title:  "How to quickly crawl a website and create a search engine"
date:   2013-12-04 10:00:00
categories: tutorial
---

Thanks to this easy to understand tutorial you will quickly learn several awesome features of [OpenSearchServer](http://www.open-search-server.com/fr).

This tutorial has been made for OpenSearchServer 1.5.0.

You will:

* **crawl a website**
* **set up a search index**
* build a **search page** with **autocompletion** and **text extracts**
* **configure facets**

Here is the final result:

![Résultat final]({{ site.baseurl }}/assets/tutorial/finaldisplay_en.png)

Some pages have been prepared to help us in this tutorial. The example is a news website with 4 URL:

* [http://www.open-search-server.com/test-website/](http://www.open-search-server.com/test-website/)
  * [http://www.open-search-server.com/test-website/unemployment-is-decreasing/](http://www.open-search-server.com/test-website/unemployment-is-decreasing/)
  * [http://www.open-search-server.com/test-website/worldcup-2040/](http://www.open-search-server.com/test-website/worldcup-2040/)
  * [http://www.open-search-server.com/test-website/oscar-academy-awards/](http://www.open-search-server.com/test-website/oscar-academy-awards/)

Before starting you will need to [install OpenSearchServer](http://www.open-search-server.com/install-opensearchserver/), which will take you 3 minutes only! 

## A few definitions

Let's explain some key concepts of the search engines area:

* **Index**: this is where documents are stored, sorted and analysed with several algorithms to allow quick search.
* **Crawler**: web crawler explores websites to index their pages. It can follow every links that it finds and be limited to some patterns of URL only. It can read lots of types of document: web page, files, images, etc. There are also crawlers for filesystem and databases.
* **Schema**: it is the index'structure. It defines the fields of indexed documents.
* **Query**: queries are the full-text search queries. Several parameters can be configured in queries, specially which fields to search in, weight for each field, facets, snippets, etc. 
* **Facet**: facets are counter with filter, on particular fields. 
* **Snippet**: snippets are extracts of text that contain the searched keywords. 
* **Renderer**: in OpenSearchServer renderer are search pages that are extremely easy to set up and customize. Those page can then be embedded in your websites to let your users search your index. 
* **Parser**: parser extract structured information from indexed documents (title, author, description, ...) 
* **Analyzer**: analyzer are customizables components that can execute lots of process on the indexed or searched texts (split texts in tokens, remove accents or plural, ...)  
* **Scheduler** : OpenSearchServer's scheduler is a powerful and highly customizable tasks manager 

This picture shows those main concepts:

![Schéma global]({{ site.baseurl }}/assets/tutorial/schema3_en.png)

Now that everything is crystal clear let's start!

## Set up crawl and index documents

### Index creation and first configuration

Let's start by creating an `index`. As explained the index is the heart of OpenSearchServer. It will store every `document` that are submitted.

* Name : `site`
* Template : `web crawler`

Click on `Create`.

![Index creation]({{ site.baseurl }}/assets/tutorial/1.create_index.png)

Chosen template, `Web crawler`, will quickly give us a powerfully configured index. this template embed a `query`, a `renderer`, a `schema` and an `HTML parser` that are greatly effective. 

Index is immediately created. You can see that several tabs are added to the main window.

![Tabs for the main index]({{ site.baseurl }}/assets/tutorial/2.tabs.png)

Select tab `Schema`. The schema defines the fields of an index.

A field has 5 properties:

* **Name**: name of the field
* **Indexed**: whether to index the value or not. If value is indexed queries will be able to search into this field. 
* **Stored** : whether to store the value or not. If value is stored queries will be able to return it as it was when submitted to the index, without the transformations made by the indexation.
* **TermVector** : this property allow, or not, use of `snippets` on this field.
* **Analyzer** : defines which `analyzer` to use on this field. 

Index has been created with lots of fields.
As you can see some fields are indexed, other stored, etc. This configuration has been made by the OpenSearchServer's team to offer the most effective search.

![Fields of the schema]({{ site.baseurl }}/assets/tutorial/3.schema_fields.png)

### HTML parser configuration

How can the crawler know in which field of the schema store each information from a web page? This is the role of the `HTML parser`.

Click on tab `Parser list` under the tab `Schema`. This pages lists every available parsers. Click on the `Edit` button of the line `HTML parser`.
Then click on tab `Field mapping`.

Here again you can see that lots of mapping are already configured. On the left of each line is an information that the parser extracts from the page, and then is the field of the schema in which this information must go. 

![Field mapping]({{ site.baseurl }}/assets/tutorial/4.parser_mapping.png)

### Crawl configuration

The web crawler needs to be configured in order to crawl our example pages.

Select tab `Crawler`. Make sure tab `Web` is selected then.

In the tab `Pattern list` we will configure which URL we want the crawler to explore.

![Pattern d'URL]({{ site.baseurl }}/assets/tutorial/5.crawler.png)

We want to crawl this website: [http://www.open-search-server.com/test-website/](http://www.open-search-server.com/test-website/). 
We can see that this page has links towards every news page. Thus we can tell the crawler to start by this page only, it will discover the links to the others pages. 

In the textarea write `http://www.open-search-server.com/test-website/*` and then click on the button `Add`.

The `/*` part tells the crawler to explore every page for which URL starts with `http://www.open-search-server.com/test-website/`.

Since every news page is "under" the main page in term of URL it will works fine. 

![Pattern d'URL]({{ site.baseurl }}/assets/tutorial/6.crawler_patterns_en.png)

### Crawl start

To start the crawler select tab `Crawl process`. Here several parameters can be adjusted. For example write `7` in field `Delay between each successive access, in seconds:`, `5` in field `Fetch interval between re-fetches:` and select `minutes` in the list. 

In the block `Current status` choose `Run forever` in the list and then click on the button `Not running - click to run`.

Process automatically updates in the area below.

![Pattern d'URL]({{ site.baseurl }}/assets/tutorial/7.run_crawler.png)

> Tab `Manual crawl` will allow you to quickly and easily test the crawler for one particular URL.

## Search content and customize relevancy

### Full-text search query

Click on tab `Query`. Click on button `Edit` for the ligne `search`.

Queries are used to search for content into the index. Search is made in configured fields, according to the weight each field is given.

As you can see query is made in numerous fields, with some differences for weights. You can easily change weight for each field to change the relevancy of each document.

![Searched fields]({{ site.baseurl }}/assets/tutorial/8.query.png)

Tab `Snipppets` lists the configured extracts of text for this query.

![Snippets]({{ site.baseurl }}/assets/tutorial/9.snippets.png)

You can also easily add some `facets` to this query. To do so go to tab `Facets` and for instance add a facet on field `host`.
In field `minimal count` write `1`  in order to show only values for which more than 1 document can be found.

![Facet]({{ site.baseurl }}/assets/tutorial/13.facet.png)

To save those modifiation click on the `Save` button on the top right of the page.

### Build a search page

So far you created an index, crawled some pages and configured a query.

Let's now see how our documents could be exposed to our visitors.

Click on tab `Renderer`. One `Renderer` is already created by the template of index we chose sooner. Click on `Edit`.

![Renderer]({{ site.baseurl }}/assets/tutorial/10.renderer.png)

You can see that this renderer uses the query `search`.

![Renderer]({{ site.baseurl }}/assets/tutorial/11.renderer_setting.png)

Tab `Fields` let you choose which fields you want to display for each result on the results page. Tab `CSS Style` give you the ability to customize the way this page will be displayed.

Click on `Save & close` and then click on the `View` button to open the renderer in a new window.

Try to search for something, for instance `coupe`. Voilà! some documents are found and displayed, with a link to the web page and some snippets. 

![Renderer]({{ site.baseurl }}/assets/tutorial/12.renderer_view_en.png)

You can also see that autocompletion is working and that the `host` facet is there on the left!

## What next?

We just discovered some of the numerous features of OpenSearchServer.

You could now read our [documentation center](http://www.open-search-server.com/confluence/display/EN/Home), to understand lots of other available parameters.

Don't forget to have a look at [our APIs](https://github.com/jaeksoft/opensearchserver/wiki)! They will allow you to easily build a powerful customized application!
