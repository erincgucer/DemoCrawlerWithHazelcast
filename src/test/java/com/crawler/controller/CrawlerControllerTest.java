package com.crawler.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * Created by egucer on 03-Jun-17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackageClasses = {CrawlerController.class})
@EnableAutoConfiguration
public class CrawlerControllerTest {

    @Autowired
    private CrawlerController crawlerController;

    @Test
    public void crawlTest() {

        Long depth = new Long(1);
        Long resultDepth = crawlerController.crawl("http://www.google.com.tr", depth);

        assertTrue(resultDepth <= depth);

    }


}
