package com.example.weather;

public class RssResponse {

    private int uniqueID;
    private String title;
    private String description;
    private String link;
    private String published;
    private boolean isDefault = true;

    public RssResponse()
    {
        this.title = "default";
        this.description = "default";
        this.link = "default";
        this.published = "default";
    }

    public RssResponse(String title, String description, String link, String published)
    {
        this.title = title;
        this.description = description;
        this.link = link;
        this.published = published;
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

}
