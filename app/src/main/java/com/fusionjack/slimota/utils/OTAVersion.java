package com.fusionjack.slimota.utils;

import android.content.Context;

import com.fusionjack.slimota.core.OTAConfig;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by fusionjack on 02.05.15.
 */
public final class OTAVersion {

    private Context mContext = null;
    private OTAConfig mConfig = null;

    public OTAVersion(Context context) {
        this.mContext = context;
        mConfig = OTAConfig.getInstance(mContext);
    }

    public String getDelimiter() {
        if (mContext != null) {
            return mConfig.getProperty(OTAConfig.VERSION_DELIMITER);
        }
        return "";
    }

    public int getPosition() {
        int position = -1;
        if (mContext != null) {
            try {
                position = Integer.parseInt(mConfig.getProperty(OTAConfig.VERSION_POSITION));
            } catch (NumberFormatException e) {
                position = -1;
            }
        }
        return position;
    }

    public SimpleDateFormat getFormat() {
        if (mContext != null) {
            String format = mConfig.getProperty(OTAConfig.VERSION_FORMAT);
            return new SimpleDateFormat(format, Locale.US);
        }
        return null;
    }
}
