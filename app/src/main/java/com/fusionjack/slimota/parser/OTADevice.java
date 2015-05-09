package com.fusionjack.slimota.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fusionjack on 30.04.15.
 */
public class OTADevice {

    private List<OTALink> mLinks;
    private String mLatestVersion;

    public OTADevice() {
        mLinks = new ArrayList<>();
    }

    public void addLink(OTALink link) {
        mLinks.add(link);
    }

    public List<OTALink> getLinks() {
        return mLinks;
    }

    public void setLatestVersion(String latestVersion) {
        this.mLatestVersion = latestVersion;
    }

    public String getLatestVersion() {
        return mLatestVersion;
    }
}
