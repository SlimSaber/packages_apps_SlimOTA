package com.fusionjack.slimota.parser;

/**
 * Created by fusionjack on 30.04.15.
 */
public class OTADevice {

    private String filename = null;
    private String downloadUrl = null;
    private String changelogUrl = null;
    private String gappsUrl = null;

    public OTADevice(String filename, String downloadUrl, String changelogUrl, String gappsUrl) {
        this.filename = filename;
        this.downloadUrl = downloadUrl;
        this.changelogUrl = changelogUrl;
        this.gappsUrl = gappsUrl;
    }

    public String getFilename() {
        return filename;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getChangelogUrl() {
        return changelogUrl;
    }

    public String getGappsUrl() {
        return gappsUrl;
    }
}
