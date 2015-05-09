package com.fusionjack.slimota.utils;

import android.content.Context;

import com.fusionjack.slimota.core.OTAConfig;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by fusionjack on 02.05.15.
 */
public final class OTAVersion {

    private OTAConfig mConfig = null;

    public OTAVersion(Context context) {
        mConfig = OTAConfig.getInstance(context);
    }

    public String getDelimiter() {
        return mConfig.getProperty(OTAConfig.VERSION_DELIMITER);
    }

    public int getPosition() {
        int position;
        try {
            position = Integer.parseInt(mConfig.getProperty(OTAConfig.VERSION_POSITION));
        } catch (NumberFormatException e) {
            position = -1;
        }
        return position;
    }

    public SimpleDateFormat getFormat() {
        String format = mConfig.getProperty(OTAConfig.VERSION_FORMAT);
        return new SimpleDateFormat(format, Locale.US);
    }
}
