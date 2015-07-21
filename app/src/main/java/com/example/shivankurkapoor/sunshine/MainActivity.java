package com.example.shivankurkapoor.sunshine;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.SpinnerAdapter;

import java.lang.reflect.Field;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback{
    private final String TAG = MainActivity.class.getSimpleName();
    //private final String FORECASTFRAGMENT_TAG = "FFTAG";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private String mLocation;
    private String munit;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);
        munit = Utility.getPrefferedUnit(this);
        makeActionOverflowMenuShown();
        setTitle();
        setContentView(R.layout.activity_main);



        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    public void setTitle()
    {
        String title = Utility.getPreferredLocation(this);
        if(title!=null)
        {
            title = title.trim();
            if(title.contains(",")){
                String temp[]=title.split(",");
                Character c= temp[0].charAt(0);
                temp[0]=temp[0].replace(temp[0].charAt(0),Character.toUpperCase(temp[0].charAt(0)));
                temp[1]=temp[1].toUpperCase();
                title = temp[0]+", "+temp[1];
            }
            setTitle("Sunshine - "+title);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        setTitle();
        ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);

        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            if (null != ff) {
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);

            if ( null != df ) {
                df.onLocationChanged(location);
            }
            mLocation = location;
        }

        String unit = Utility.getPrefferedUnit(this);
        if (unit != null & !unit.equals(munit)) {
            if (null != ff) {
                ff.onUnitChanged();
            }
            munit = unit;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));


            return true;


        }

        return super.onOptionsItemSelected(item);
    }

    private void makeActionOverflowMenuShown() {
        //devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    @Override

    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
