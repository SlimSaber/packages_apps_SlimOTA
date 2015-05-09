package com.fusionjack.slimota.xml;

/**
 * Created by fusionjack on 09.05.15.
 */
public class OTALink {

    private String mId;
    private String mTitle;
    private String mDescription;
    private String mUrl;

    public OTALink(String id) {
        this.mId = id;
    }

    public String getId() {
        return mId == null ? "" : mId;
    }

    public String getTitle() {
        return mTitle == null ? "" : mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDescription() {
        return mDescription == null ? "" : mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getUrl() {
        return mUrl == null ? "" : mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }
}
