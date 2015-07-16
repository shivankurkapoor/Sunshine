/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.example.shivankurkapoor.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shivankurkapoor.sunshine.R;

public class DetailActivity extends ActionBarActivity {

    private ShareActionProvider mShareActionProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        // Locate MenuItem with ShareActionProvider
       // MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
       // mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }



        return super.onOptionsItemSelected(item);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
            setHasOptionsMenu(true);
        }

        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private String mForecaststr;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();//getting a set intent

            TextView tv= (TextView)rootView.findViewById(R.id.fragment_detail_text);

            mForecaststr  = intent.getStringExtra(intent.EXTRA_TEXT);
            tv.setText(mForecaststr);


            return rootView;
        }

       @Override
       public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
       {
             inflater.inflate(R.menu.detailfragment,menu);

           MenuItem menuItem = menu.findItem(R.id.menu_item_share);
           // Get the provider and hold onto it to set/change the share intent.
           ShareActionProvider mShareActionProvider =
                   (ShareActionProvider)   MenuItemCompat.getActionProvider(menuItem);

           // Attach an intent to this ShareActionProvider.  You can update this at any time,
           // like when the user selects a new piece of data they might like to share.
           if (mShareActionProvider != null ) {
               mShareActionProvider.setShareIntent(createShareForecastIntent());
           } else {
               Log.d(PlaceholderFragment.class.getSimpleName(), "Share Action Provider is null?");
           }
       }

        private Intent createShareForecastIntent()
        {
            Intent shareIntent = new Intent (Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,mForecaststr+FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }
    }


}