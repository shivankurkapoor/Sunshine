package com.example.shivankurkapoor.sunshine;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.example.shivankurkapoor.sunshine.data.WeatherContract;

import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    ArrayAdapter<String> stringArrayAdapter;
    private ForecastAdapter mForecastAdapter;
    private int listposition;
    private static final String POSITION_KEY = "position";
    private static final int FORECAST_LOADER = 0;
    ListView listView;
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    public ForecastFragment() {

    }


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//
//       List<String> arr = new ArrayList<String>();
//        stringArrayAdapter =
//                new ArrayAdapter<String>(
//                //context
//                getActivity(),
//                //id of list item layout
//                R.layout.list_item_forecast,
//                //id of the text view to populate
//                R.id.list_item_forecast_textview,
//                //
//                arr);
        // View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        // final ListView listView = (ListView) rootview.findViewById(R.id.listview_forecast);
//        listView.setAdapter(stringArrayAdapter);
//
//
//        //Toast
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Context context = view.getContext();
//                //TextView tv = (TextView)view.findViewById(R.id.list_item_forecast_textview);
//                //CharSequence text = tv.getText();
//                // int duration = Toast.LENGTH_SHORT;
//
//                // Toast toast = Toast.makeText(context, text, duration);
//                // toast.show();
//                String forecast = stringArrayAdapter.getItem(position);
//
//                //Explicit Intent
//                Intent detailactivity = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, forecast);
//                startActivity(detailactivity);
//            }
//        });
//        return rootview;

        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.


        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //            @Override
//            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
//                // CursorAdapter returns a cursor at the correct position for getItem(), or null
//                // if it cannot seek to that position.
//                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
//                if (cursor != null) {
//                    String locationSetting = Utility.getPreferredLocation(getActivity());
//                    Intent intent = new Intent(getActivity(), DetailActivity.class)
//                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
//                            ));
//                    startActivity(intent);
//                }
//            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                listposition = position;
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    ((Callback) getActivity())
                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                }
            }

        });


        if(savedInstanceState!=null && savedInstanceState.containsKey(POSITION_KEY))
            listposition = savedInstanceState.getInt(POSITION_KEY);

        return rootView;


    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if(listposition!=ListView.INVALID_POSITION)
            outState.putInt(POSITION_KEY,listposition);

        super.onSaveInstanceState(outState);


    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
        inflater.inflate(R.menu.viewlocation, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            updateWeather();
            return true;
        }

        if (id == R.id.action_view_location) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_defaultvalue));

            Uri gmmIntentUri = Uri.parse("geo:0,0?").buildUpon()
                    .appendQueryParameter("q", location)
                    .build();
            Intent mapintent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapintent.setPackage("com.google.android.apps.maps");

            if (mapintent.resolveActivity(this.getActivity().getApplicationContext().getPackageManager()) != null)
                startActivity(mapintent);
            else
                Log.d(ForecastFragment.class.getSimpleName(), "Couldn't call" + location);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    void onUnitChanged() {
        mForecastAdapter.notifyDataSetChanged();
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(this.getActivity());
        String location = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(location);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());


        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        if(listposition!=ListView.INVALID_POSITION)
            listView.setSelection(listposition);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }


}

