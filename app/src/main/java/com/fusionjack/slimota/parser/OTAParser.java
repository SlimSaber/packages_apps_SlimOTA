package com.fusionjack.slimota.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fusionjack on 30.04.15.
 */
public class OTAParser {

    private static final String ns = null;
    private static final String TAG_FILENAME = "Filename";
    private static final String TAG_DOWNLOAD_URL = "DownloadUrl";
    private static final String TAG_CHANGELOG_URL = "ChangelogUrl";
    private static final String TAG_GAPPS_URL = "GappsUrl";

    private String mDeviceName = null;
    private String mReleaseType = null;
    private OTADevice mDevice = null;

    private static OTAParser mInstance;

    private OTAParser() {
    }

    public static OTAParser getInstance() {
        if (mInstance == null) {
            mInstance = new OTAParser();
        }
        return mInstance;
    }

    public OTADevice parse(InputStream in, String deviceName, String releaseType) throws XmlPullParserException, IOException {
        this.mDeviceName = deviceName;
        this.mReleaseType = releaseType;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readBuildType(parser);
            return mDevice;
        } finally {
            in.close();
        }
    }

    private void readBuildType(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equalsIgnoreCase(mReleaseType)) {
                readStable(parser);
            } else {
                skip(parser);
            }
        }
    }

    private void readStable(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equalsIgnoreCase(mDeviceName)) {
                readDevice(parser);
            } else {
                skip(parser);
            }
        }
    }

    private void readDevice(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, mDeviceName);
        String filename = null;
        String downloadUrl = null;
        String changelogUrl = null;
        String gappsUrl = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equalsIgnoreCase(TAG_FILENAME)) {
                filename = readTag(parser, TAG_FILENAME);
            } else if (name.equalsIgnoreCase(TAG_DOWNLOAD_URL)) {
                downloadUrl = readTag(parser, TAG_DOWNLOAD_URL);
            } else if (name.equalsIgnoreCase(TAG_CHANGELOG_URL)) {
                changelogUrl = readTag(parser, TAG_CHANGELOG_URL);
            } else if (name.equalsIgnoreCase(TAG_GAPPS_URL)) {
                gappsUrl = readTag(parser, TAG_GAPPS_URL);
            } else {
                skip(parser);
            }
        }
        mDevice = new OTADevice(filename, downloadUrl, changelogUrl, gappsUrl);
    }

    private String readTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return text;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
