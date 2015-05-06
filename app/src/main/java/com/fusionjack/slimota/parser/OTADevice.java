package com.fusionjack.slimota.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by fusionjack on 30.04.15.
 */
public class OTADevice {

    private Map<String, String> mUrlMap;
    private String mLatestVersion;

    public OTADevice() {
        mUrlMap = new HashMap<>();
    }

    public void addUrl(String key, String value) {
        mUrlMap.put(key, value);
    }

    public String getUrl(String key) {
        return mUrlMap.get(key);
    }

    public Set<String> getUrls() {
        return mUrlMap.keySet();
    }

    public void setLatestVersion(String latestVersion) {
        this.mLatestVersion = latestVersion;
    }

    public String getLatestVersion() {
        return mLatestVersion;
    }
}
