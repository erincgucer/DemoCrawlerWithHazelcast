package com.crawler.controller;

import com.crawler.domain.CrawlURL;
import com.crawler.task.CrawlTask;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

/**
 * Created by egucer on 03-Jun-17.
 */
@Component
public class CrawlerController {

    private Set<String> pagesVisited;
    private BlockingQueue<CrawlURL> pagesToVisit;

    private int currentHazelcastInstanceNumber = 0;
    private static final Long MAX_HAZELCAST_INSTANCE = new Long(5);

    public Long crawl(String rootUrl, Long depth) {
        HazelcastInstance firstInstance = Hazelcast.newHazelcastInstance(new Config()); // first instance of Hazelcast
        currentHazelcastInstanceNumber++;

        pagesToVisit = firstInstance.getQueue("pagesToVisit");
        pagesVisited = firstInstance.getSet("pagesVisited");

        Long currentDepth = new Long(0); // set current depth

        pagesToVisit.add(new CrawlURL(rootUrl, currentDepth));

        while (!pagesToVisit.isEmpty()) { // will proceed at least once (for rootUrl)

            CrawlURL currentUrl = pagesToVisit.remove();

            if (currentUrl.getDepth() <= depth && !pagesVisited.contains(currentUrl.getUrl())) {

                List<String> foundURLs = new ArrayList<>();

                try {
                    // execute CrawlTask on the member Hazelcast will pick
                    IExecutorService executorService = firstInstance.getExecutorService("default");
                    Future<List<String>> future = executorService.submit(new CrawlTask(currentUrl));

                    foundURLs = future.get(); // get results of CrawlTask

                } catch (Exception e) {
                    System.out.println("An Error Occured. " + e.getLocalizedMessage());
                }

                pagesVisited.add(currentUrl.getUrl());

                for (String url : foundURLs) {

                    if (!pagesVisited.contains(url) && !pagesToVisit.contains(url)) {  // only if the url found is completely new

                        // for each 10 URL, create a new Hazelcast instance up to a maximum number
                        if (currentHazelcastInstanceNumber < MAX_HAZELCAST_INSTANCE && pagesToVisit.size() >= currentHazelcastInstanceNumber * 10) {
                            Hazelcast.newHazelcastInstance(new Config()).getName();
                            currentHazelcastInstanceNumber++;
                        }

                        currentDepth = currentUrl.getDepth() + 1;
                        pagesToVisit.add(new CrawlURL(url, currentDepth));
                    }
                }
            }
        }

        return --currentDepth;

    }
}
