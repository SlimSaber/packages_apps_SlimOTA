/*
 * Copyright (C) 2015 Chandra Poerwanto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fusionjack.slimota.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.fusionjack.slimota.configs.OTAConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class OTAUtils {

    private static final String TAG = "SlimOTA";
    private static final boolean DEBUG = true;

    private OTAUtils() {
    }

    public static void logError(Exception e) {
        if (DEBUG) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void logInfo(String message) {
        if (DEBUG) {
            Log.i(TAG, message);
        }
    }

    public static boolean checkServerVersion(String serverVersion, Context context) {
        if (context == null) {
            return false;
        }

        String localVersion = OTAConfig.getInstance(context).getCurrentVersion();
        OTAUtils.logInfo("serverVersion: " + serverVersion);
        OTAUtils.logInfo("localVersion: " + localVersion);
        if (serverVersion.isEmpty() || localVersion.isEmpty()) {
            return false;
        }

        return compareVersion(localVersion, serverVersion, context);
    }

    public static boolean compareVersion(String localVersion, String serverVersion, Context context) {
        final String delimiter = OTAConfig.getInstance(context).getDelimiter();
        final int position = OTAConfig.getInstance(context).getPosition();
        final SimpleDateFormat format = OTAConfig.getInstance(context).getFormat();

        String[] localTokens = localVersion.split(delimiter);
        String[] serverTokens = serverVersion.split(delimiter);
        if (position > -1 && position < localTokens.length && position < serverTokens.length) {
            String localDate = localTokens[position];
            String serverDate = serverTokens[position];
            return isVersionNewer(serverDate, localDate, format);
        }
        return false;
    }

    private static boolean isVersionNewer(String serverVersion, String currentVersion,
                                          final SimpleDateFormat format) {
        boolean versionIsNew = false;
        if (format == null || serverVersion.isEmpty() || currentVersion.isEmpty()) {
            return versionIsNew;
        }
        try {
            Date serverDate = format.parse(serverVersion);
            Date currentDate = format.parse(currentVersion);
            versionIsNew = serverDate.after(currentDate);
        } catch (ParseException e) {
            logError(e);
        }
        return versionIsNew;
    }

    public static String getProperty(String property) {
        Process process = null;
        BufferedReader buff = null;
        try {
            process = Runtime.getRuntime().exec("getprop " + property);
            buff = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return buff.readLine();
        } catch (IOException e) {
            logError(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
            try {
                if (buff != null) {
                    buff.close();
                }
            } catch (IOException e) {
                logError(e);
            }
        }
        return "";
    }

    public static InputStream downloadURL(String link) throws IOException {
        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        logInfo("downloadStatus: " + conn.getResponseCode());
        return conn.getInputStream();
    }

    public static void launchUrl(String url, Context context) {
        if (!url.isEmpty() && context != null) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }
}
