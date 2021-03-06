package com.zmb.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zmb.sunshine.data.db.WeatherContract;

/**
 * A custom CursorAdapter that allows today's forecast
 * to use a different layout than the other days.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE = 1;

    // determines whether the forecast for today uses a special view
    private boolean mUseDifferentTodayView = true;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setUseDifferentTodayView(boolean useDifferentView) {
        mUseDifferentTodayView = useDifferentView;
    }

    @Override
    public int getViewTypeCount() {
        // 2 different views - one for today, and one for other days
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // on large devices (multi-pane) we always use the same view
        // on phones, we make the view for today a little bit bigger
        if (mUseDifferentTodayView) {
            return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE;
        }
        return VIEW_TYPE_FUTURE;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layout = getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY ?
                R.layout.list_item_today :
                R.layout.list_item_forecast;

        View root = LayoutInflater.from(context).inflate(layout, parent, false);

        // create a view holder to avoid repeated findViewById() calls
        ViewHolder holder = new ViewHolder(root);
        root.setTag(holder);
        return root;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        // use the art for the first view (today),
        // and the regular icon for other days
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        WeatherContract.WeatherId id = WeatherContract.WeatherId.fromInt(weatherId);
        int imageId = cursor.getPosition() == 0 ?
                Sunshine.getArtForWeatherId(id) :
                Sunshine.getIconForWeatherId(id);
        holder.mImageView.setImageResource(imageId);

        String date = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        holder.mDateView.setText(Sunshine.friendlyDate(context, date));

        String desc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        holder.mForecastView.setText(desc);

        boolean isMetric = Sunshine.isMetric(context);

        double maxTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_HIGH);
        holder.mHighView.setText(Sunshine.formatTemperature(context, maxTemp, isMetric));

        double minTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_LOW);
        holder.mLowView.setText(Sunshine.formatTemperature(context, minTemp, isMetric));
    }

    public static class ViewHolder {
        public final ImageView mImageView;
        public final TextView mDateView;
        public final TextView mForecastView;
        public final TextView mHighView;
        public final TextView mLowView;

        public ViewHolder(View parent) {
            mImageView = (ImageView) parent.findViewById(R.id.list_item_icon);
            mDateView = (TextView) parent.findViewById(R.id.list_item_date_textview);
            mForecastView = (TextView) parent.findViewById(R.id.list_item_forecast_textview);
            mHighView = (TextView) parent.findViewById(R.id.list_item_high_textview);
            mLowView = (TextView) parent.findViewById(R.id.list_item_low_textview);
        }
    }
}
