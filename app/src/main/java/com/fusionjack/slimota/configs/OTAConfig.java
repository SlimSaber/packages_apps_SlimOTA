package com.fusionjack.slimota.configs;

import android.content.Context;

import com.fusionjack.slimota.utils.OTAUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by fusionjack on 08.05.15.
 */
public class OTAConfig extends Properties {

    private final static String FILENAME = "ota_conf";

    private final static String OTA_URL = "ota_url";
    private final static String RELEASE_TYPE = "release_type";

    private final static String DEVICE_NAME = "device_name";

    private final static String VERSION_NAME = "version_name";
    private final static String VERSION_DELIMITER = "version_delimiter";
    private final static String VERSION_FORMAT = "version_format";
    private final static String VERSION_POSITION = "version_position";

    private static String mCurrentVersion = "";
    private static String mDeviceName = "";

    private static OTAConfig mInstance;

    private OTAConfig() {
    }

    public static OTAConfig getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new OTAConfig();
            try {
                InputStream is = context.getAssets().open(FILENAME);
                mInstance.load(is);
                is.close();
            } catch (IOException e) {
                OTAUtils.logError(e);
            }
        }
        return mInstance;
    }

    public String getOtaUrl() {
        return getProperty(OTAConfig.OTA_URL);
    }

    public String getReleaseType() {
        return getProperty(OTAConfig.RELEASE_TYPE);
    }

    public String getCurrentVersion() {
        if (mCurrentVersion.isEmpty()) {
            String propName = getProperty(VERSION_NAME);
            mCurrentVersion = OTAUtils.getProperty(propName);
        }
        return mCurrentVersion;
    }

    public String getDeviceName() {
        if (mDeviceName.isEmpty()) {
            String propName = getProperty(OTAConfig.DEVICE_NAME);
            mDeviceName = OTAUtils.getProperty(propName);
        }
        return mDeviceName;
    }

    public String getDelimiter() {
        return getProperty(OTAConfig.VERSION_DELIMITER);
    }

    public int getPosition() {
        int position;
        try {
            position = Integer.parseInt(getProperty(OTAConfig.VERSION_POSITION));
        } catch (NumberFormatException e) {
            position = -1;
        }
        return position;
    }

    public SimpleDateFormat getFormat() {
        String format = getProperty(OTAConfig.VERSION_FORMAT);
        return new SimpleDateFormat(format, Locale.US);
    }
}
