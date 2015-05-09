package com.fusionjack.slimota.core;

import android.content.Context;

import com.fusionjack.slimota.parser.OTALink;
import com.fusionjack.slimota.parser.OTAParser;
import com.fusionjack.slimota.utils.OTAUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fusionjack on 09.05.15.
 */
public class LinkConfig {

    private static final String FILENAME = "links_conf";

    private static LinkConfig mInstance;
    private static List<OTALink> mLinks;

    private LinkConfig() {
    }

    public static LinkConfig getInstance() {
        if (mInstance == null) {
            mInstance = new LinkConfig();
        }
        return mInstance;
    }

    public static void persistLinks(List<OTALink> links, Context context) {
        try {
            File dir = context.getFilesDir();
            File file = new File(dir, FILENAME);
            if (file.exists()) {
                file.delete();
            }

            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);

            JSONArray jsonLinks = new JSONArray();
            for (OTALink link : links) {
                JSONObject jsonLink = new JSONObject();
                jsonLink.put(OTAParser.ID, link.getId());
                jsonLink.put(OTAParser.TITLE, link.getTitle());
                jsonLink.put(OTAParser.DESCRIPTION, link.getDescription());
                jsonLink.put(OTAParser.URL, link.getUrl());
                jsonLinks.put(jsonLink);
            }

            fos.write(jsonLinks.toString().getBytes());
            fos.close();

            LinkConfigListener listener = getLinkConfigListener(context);
            if (listener != null) {
                listener.onConfigChange();
            }
        } catch (IOException | JSONException e) {
            OTAUtils.logError(e);
        }
    }

    public List<OTALink> getLinks(Context context, boolean force) {
        if (mLinks == null || force) {
            try {
                mLinks = new ArrayList<>();

                FileInputStream fis = context.openFileInput(FILENAME);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                StringBuffer out = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();
                fis.close();

                JSONArray jsonLinks = new JSONArray(out.toString());
                for (int i = 0; i < jsonLinks.length(); i++) {
                    JSONObject jsonLink = jsonLinks.getJSONObject(i);
                    OTALink link = new OTALink(jsonLink.getString(OTAParser.ID));
                    link.setTitle(jsonLink.getString(OTAParser.TITLE));
                    link.setDescription(jsonLink.getString(OTAParser.DESCRIPTION));
                    link.setUrl(jsonLink.getString(OTAParser.URL));
                    mLinks.add(link);
                }
            } catch (JSONException | IOException e) {
                OTAUtils.logError(e);
            }
        }
        return mLinks;
    }

    public static OTALink findLink(String linkId, Context context) {
        List<OTALink> links = LinkConfig.getInstance().getLinks(context, false);
        for (OTALink link : links) {
            if (link.getId().equalsIgnoreCase(linkId)) {
                return link;
            }
        }
        return null;
    }

    public interface LinkConfigListener {
        void onConfigChange();
    }

    private static LinkConfigListener getLinkConfigListener(Context context) {
        if (context instanceof LinkConfigListener) {
            return (LinkConfigListener) context;
        }
        return null;
    }
}
