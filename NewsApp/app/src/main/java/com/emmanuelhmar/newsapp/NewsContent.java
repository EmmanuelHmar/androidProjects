package com.emmanuelhmar.newsapp;

import java.net.URI;

public class NewsContent {
    private String type, sectionName, pillarName, webTitle, date;
    private URI webURL;

    public NewsContent(String type, String sectionName, String pillarName, String webTitle, String date, URI webURL) {
        this.type = type;
        this.sectionName = sectionName;
        this.pillarName = pillarName;
        this.webTitle = webTitle;
        this.date = date;
        this.webURL = webURL;
    }

    public String getType() {
        return type;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getPillarName() {
        return pillarName;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getDate() {
        return date;
    }

    public URI getWebURL() {
        return webURL;
    }
}
