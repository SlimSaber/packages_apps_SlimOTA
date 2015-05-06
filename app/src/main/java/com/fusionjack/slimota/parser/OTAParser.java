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
    private static final String FILENAME_TAG = "Filename";
    private static final String URL_TAG = "Url";

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

    public static boolean isUrlKey(String key) {
        return key.toLowerCase().endsWith(URL_TAG.toLowerCase());
    }

    public static String stripUrlfromKey(String key) {
        int index = key.toLowerCase().indexOf(URL_TAG.toLowerCase());
        if (index > 0) {
            return key.substring(0, index);
        }
        return key;
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
        mDevice = new OTADevice();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            String tagValue = readTag(parser, tagName);
            if (tagName.equalsIgnoreCase(FILENAME_TAG)) {
                mDevice.setLatestVersion(tagValue);
            } else if (isUrlKey(tagName)) {
                mDevice.addUrl(tagName, tagValue);
            } else {
                skip(parser);
            }
        }
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
