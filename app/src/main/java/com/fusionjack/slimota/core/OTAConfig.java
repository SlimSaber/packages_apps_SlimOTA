package com.fusionjack.slimota.core;

import android.content.Context;

import com.fusionjack.slimota.utils.OTAUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by fusionjack on 08.05.15.
 */
public class OTAConfig extends Properties {

    private final static String OTA_CONF = "ota_conf";

    public final static String OTA_URL = "ota_url";
    public final static String RELEASE_TYPE = "release_type";
    public final static String DEVICE_NAME = "device_name";
    public final static String VERSION_NAME = "version_name";
    public final static String VERSION_DELIMITER = "version_delimiter";
    public final static String VERSION_FORMAT = "version_format";
    public final static String VERSION_POSITION = "version_position";

    private static OTAConfig mInstance;

    private OTAConfig() {
    }

    public static OTAConfig getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new OTAConfig();
            try {
                InputStream is = context.getAssets().open(OTA_CONF);
                if (is != null) {
                    mInstance.load(is);
                    is.close();
                }
            } catch (IOException e) {
                OTAUtils.logError(e);
            }
        }
        return mInstance;
    }
}
