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

package com.fusionjack.slimota.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fusionjack.slimota.R;
import com.fusionjack.slimota.utils.Changelog;

public class ChangelogFragment extends PreferenceFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.build_changelog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Changelog rc = new Changelog() {
            @Override
            public void onResponseReceived(String result) {
                final TextView changelogTextView = (TextView) getView().findViewById(R.id.tv_changelog);
                if (changelogTextView == null) {
                    return;
                }
                changelogTextView.setText(result);
            }
        };
        rc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity());
    }
}
