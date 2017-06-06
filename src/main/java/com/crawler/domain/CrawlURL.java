package com.crawler.domain;

import java.io.Serializable;

/**
 * Created by egucer on 05-Jun-17.
 */
public class CrawlURL implements Serializable {
    private String url;
    private Long depth;

    public CrawlURL(String url, Long depth) {
        this.url = url;
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getDepth() {
        return depth;
    }

    public void setDepth(Long depth) {
        this.depth = depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrawlURL crawlURL = (CrawlURL) o;

        return url.equals(crawlURL.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
