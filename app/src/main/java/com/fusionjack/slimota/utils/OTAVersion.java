package com.fusionjack.slimota.utils;

import android.content.Context;

import com.fusionjack.slimota.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by fusionjack on 02.05.15.
 */
public final class OTAVersion {

    private Context mContext = null;

    public OTAVersion(Context context) {
        this.mContext = context;
    }

    public String getDelimiter() {
        if (mContext != null) {
            return mContext.getResources().getString(R.string.version_delimiter);
        }
        return "";
    }

    public int getPosition() {
        int position = -1;
        if (mContext != null) {
            try {
                position = Integer.parseInt(mContext.getResources().getString(R.string.version_position));
            } catch (NumberFormatException e) {
                position = -1;
            }
        }
        return position;
    }

    public SimpleDateFormat getFormat() {
        if (mContext != null) {
            String format = mContext.getResources().getString(R.string.version_format);
            return new SimpleDateFormat(format, Locale.US);
        }
        return null;
    }
}
