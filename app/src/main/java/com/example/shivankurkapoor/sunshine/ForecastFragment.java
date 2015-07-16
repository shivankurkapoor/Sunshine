package com.example.shivankurkapoor.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    ArrayAdapter<String> stringArrayAdapter;


    public ForecastFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        List<String> arr = new ArrayList<String>();
        stringArrayAdapter =
                new ArrayAdapter<String>(
                //context
                getActivity(),
                //id of list item layout
                R.layout.list_item_forecast,
                //id of the text view to populate
                R.id.list_item_forecast_textview,
                //
                arr);
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView listView = (ListView) rootview.findViewById(R.id.listview_forecast);
        listView.setAdapter(stringArrayAdapter);


        //Toast
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                //TextView tv = (TextView)view.findViewById(R.id.list_item_forecast_textview);
                //CharSequence text = tv.getText();
                // int duration = Toast.LENGTH_SHORT;

                // Toast toast = Toast.makeText(context, text, duration);
                // toast.show();
                String forecast = stringArrayAdapter.getItem(position);

                //Explicit Intent
                Intent detailactivity = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(detailactivity);
            }
        });
        return rootview;


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

        if(id == R.id.action_view_location)
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_defaultvalue));

            Uri gmmIntentUri = Uri.parse("geo:0,0?").buildUpon()
                    .appendQueryParameter("q",location)
                    .build();
            Intent mapintent = new Intent(Intent.ACTION_VIEW);
            mapintent.setPackage("com.google.android.apps.maps");

           if(mapintent.resolveActivity(this.getActivity().getApplicationContext().getPackageManager())!=null)
            startActivity(mapintent);
           else
           Log.d(ForecastFragment.class.getSimpleName(), "Couldn't call" + location);
            return true;
        }




       /* else if(id == R.id.action_settings)
        {
            Intent setting = new Intent(getActivity(), SettingsActivity.class);

            startActivity(setting);
        }*/

        return super.onOptionsItemSelected(item);
    }


    private void updateWeather()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
       //SharedPreferences.Editor editor = preferences.edit();
       //editor.putString("location", "Hyderabad,IN");
       //editor.commit();
        String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_defaultvalue));

        if(location!=null)
            new FetchWeatherTask().execute(location);

    }

    @Override
    public void onStart()
    {
        super.onStart();
        updateWeather();
    }
    public class FetchWeatherTask extends AsyncTask <String,Void,String[]>{


        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        @Override

        protected String[] doInBackground(String... params) {
                    // Establishing Connections
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String format = "json";
            String units = "metric";
            int numDays = 16;

            try{
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();

               // Log.v(LOG_TAG,"URL @@@@@" + builtUri.toString());

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream ==null)
                {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line=reader.readLine())!=null)
                {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.

                    buffer.append(line+"\n");
                }

                if(buffer.length()==0)
                    //no data received
                    return null;

                forecastJsonStr = buffer.toString();

              //  Log.v(LOG_TAG,"Forecast Json String" +forecastJsonStr);

            }
            catch(IOException e)
            {
                Log.e(LOG_TAG,"Error",e);

                return null;
            }

            finally {
                if(urlConnection!=null)
                    urlConnection.disconnect();
                if(reader!=null)
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                try {
                    return this.getWeatherDataFromJson(forecastJsonStr, numDays);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String[] weatherforcasts) {
            super.onPostExecute(weatherforcasts);
            List<String> arr = new ArrayList<String>(Arrays.asList(weatherforcasts));
            stringArrayAdapter.clear();
            stringArrayAdapter.addAll(weatherforcasts);

        }

        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low,String unitType) {
            // For presentation, assume the user doesn't care about tenths of a degree.


            long roundedHigh;
            long roundedLow;
            if(unitType.equals("imperial")) {
                   roundedHigh = Math.round((high*1.8)+32);
                   roundedLow = Math.round((low*1.8)+32);
            }

            else{
                  roundedHigh = Math.round(high);
                  roundedLow = Math.round(low);
            }
            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPrefs.getString(
                    getString(R.string.pref_units_key),
                    getString(R.string.pref_units_metric));



            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low,unitType);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

          /*  for (String s : resultStrs) {
              //  Log.v(LOG_TAG, "Forecast entry: " + s);
            }*/
            return resultStrs;

        }




        }
    }

