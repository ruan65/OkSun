package idea.ruan.oksun;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static idea.ruan.oksun.ForecastFragment.COL_WEATHER_DATE;
import static idea.ruan.oksun.ForecastFragment.COL_WEATHER_DESC;
import static idea.ruan.oksun.ForecastFragment.COL_WEATHER_MAX_TEMP;
import static idea.ruan.oksun.ForecastFragment.COL_WEATHER_MIN_TEMP;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int itemLayoutId = getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY ?
                R.layout.list_item_forecast_today : R.layout.list_item_forecast;

        View returnedView = LayoutInflater.from(context).inflate(itemLayoutId, parent, false);
        returnedView.setTag(new ViewHolder(returnedView));

        return returnedView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder h = (ViewHolder) view.getTag();

        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);

        h.iconView.setImageResource(R.mipmap.ic_launcher);

        h.descriptionView.setText(cursor.getString(COL_WEATHER_DESC));

        long dateInMillis = Long.parseLong(cursor.getString(COL_WEATHER_DATE));

        h.dateView.setText(Utility.getFriendlyDayString(context, dateInMillis));

        boolean isMetric = Utility.isMetric(context);

        h.high.setText(Utility.formatTemperature(
                mContext, cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric));

        h.low.setText(Utility.formatTemperature(
                mContext, cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric));
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView high;
        public final TextView low;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            high = (TextView) view.findViewById(R.id.list_item_high_textview);
            low = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}
