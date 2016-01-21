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
import android.os.AsyncTask;

import com.fusionjack.slimota.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

interface IChangelog {
    public void onResponseReceived(String result);
}

public abstract class Changelog extends AsyncTask<Context, Integer, String> implements IChangelog {

    private static final String CHANGELOG_FILE = "/system/etc/changelog.txt";

    public abstract void onResponseReceived(String result);

    @Override
    protected String doInBackground(Context... arg) {
        Context context = arg[0];
        File file = new File(CHANGELOG_FILE);
        if (!file.exists()) {
            return context.getString(R.string.no_changelog_summary);
        }

        StringBuilder sb = new StringBuilder();
        Scanner scanner = null;
        String changelog;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                sb.append(line);
                sb.append("\n");
            }
            changelog = sb.toString();
        } catch (FileNotFoundException ex) {
            changelog = ex.getMessage();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return changelog;
    }

    @Override
    protected void onPostExecute(String result) {
        onResponseReceived(result);
    }
}
