package com.example.shivankurkapoor.sunshine;

/**
 * Created by Shivankur Kapoor on 18-07-2015.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shivankurkapoor.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TODAY = 0;
    private final int VIEW_FUTURE = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TODAY : VIEW_FUTURE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor


        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutid = -1;
        if (viewType == VIEW_TODAY)
            layoutid = R.layout.list_item_forecast_today;
        else if (viewType == VIEW_FUTURE)
            layoutid = R.layout.list_item_forecast;
        View view = LayoutInflater.from(context).inflate(layoutid, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }


    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int weather_id = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);

//        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
//        iconView.setImageResource(R.drawable.ic_launcher);
        int viewType = getItemViewType(cursor.getPosition());

        Log.v(ForecastAdapter.class.getSimpleName(),"@@@@###$$$%%%"+Utility.getArtResourceForWeatherCondition(weather_id));
        switch (viewType) {
            case VIEW_TODAY: {
                // Get weather icon
                viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weather_id));
                break;
            }
            case VIEW_FUTURE: {
                // Get weather icon
                viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weather_id));
                break;
            }
        }


        long dateinmilli = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
//        TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
//        dateView.setText(Utility.getFriendlyDayString(context, dateinmilli));

        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateinmilli));

        String desc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
//        TextView descView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
//        descView.setText(desc);

        viewHolder.descriptionView.setText(desc);


        boolean isMetric = Utility.isMetric(context);
        String temphigh = Utility.formatTemperature(cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP), isMetric);
        String templow = Utility.formatTemperature(cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP), isMetric);
//        TextView highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
//        TextView lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
//        highTempView.setText(temphigh + (char) 0x00B0);
//        lowTempView.setText(templow + (char) 0x00B0);

        viewHolder.highTempView.setText(temphigh + (char) 0x00B0);
        viewHolder.lowTempView.setText(templow + (char) 0x00B0);


    }
}