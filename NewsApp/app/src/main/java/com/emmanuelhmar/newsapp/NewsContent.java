package com.emmanuelhmar.newsapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsContent {
//    @SerializedName("type")
    @SerializedName("type")
    @Expose
    private String type;
    @Expose
    @SerializedName("sectionName")
//    @SerializedName("login")
    private String sectionName;
    @SerializedName("pillarName")
//    @SerializedName("site_admin")
    @Expose
    private String pillarName;
    @SerializedName("webTitle")
//    @SerializedName("id")
    @Expose
    private String webTitle;
    @SerializedName("webPublicationDate")
//    @SerializedName("node_id")
    @Expose
    private String date;
    @SerializedName("webUrl")
//    @SerializedName("avatar_url")
    @Expose
    private String webURL;

    public NewsContent(String type, String sectionName, String pillarName, String webTitle, String date, String webURL) {
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

        return date.substring(0, date.indexOf("T"));
    }

    public String getWebURL() {
        return webURL;
    }
}
