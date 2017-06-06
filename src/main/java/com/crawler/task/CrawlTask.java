package com.crawler.task;

import com.crawler.domain.CrawlURL;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by egucer on 05-Jun-17.
 */
public class CrawlTask implements Callable<List<String>>, Serializable, HazelcastInstanceAware {

    private transient HazelcastInstance localInstance;
    private CrawlURL crawlURL;
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";

    public CrawlTask(CrawlURL crawlURL) {
        this.crawlURL = crawlURL;
    }

    @Override
    public List<String> call() throws Exception {
        List<String> pagesFound = new LinkedList<String>();

        try {
            Connection connection = Jsoup.connect(crawlURL.getUrl()).userAgent(USER_AGENT); // connect to URL
            Document htmlDocument = connection.get();  // get page in URL

            if (connection.response().statusCode() == 200) { // if successful
                Elements linksOnPage = htmlDocument.select("a[href]"); // get links on page

                System.out.println("\n**Crawling web page at " + crawlURL.getUrl() + " ** " + linksOnPage.size() + " links found "
                        + "** Current depth is " + crawlURL.getDepth() + " ** Hazelcast instance name is " + localInstance.getName());

                for (Element link : linksOnPage) {
                    pagesFound.add(link.absUrl("href"));
                }
            }
            if (!connection.response().contentType().contains("text/html")) { // if content is not html
                System.out.println("**Crawl Failure **Retrieved something other than HTML");
            }

        } catch (Exception ioe) {
            System.out.println("An Error Occured. Current URL: " + crawlURL.getUrl() + "\n  Cause: " + ioe.getLocalizedMessage());
        }

        return pagesFound;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.localInstance = hazelcastInstance;
    }
}
