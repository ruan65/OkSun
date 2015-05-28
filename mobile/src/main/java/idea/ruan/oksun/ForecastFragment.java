package idea.ruan.oksun;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ForecastFragment extends Fragment {

    ArrayAdapter<String> mForecastAdapter;
    ListView listView;

    ArrayList<String> weekForecast = new ArrayList<>();

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mForecastAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast,
                R.id.list_item_forecast_textview, weekForecast);

        View rootView = inflater.inflate(R.layout.f_forecast, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.m_f_forecast, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {

            new FetchWeatherTask().execute("109451");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String createUrlString(String zipCode) {

        String format = "json";
        String units = "metric";
        int numDays = 7;

        final String FORECAST_BASE_URL ="http://api.openweathermap.org/data/2.5/forecast/daily?";
        final String QUERY_PARAM = "q";
        final String FORMAT_PARAM = "mode";
        final String UNITS_PARAM = "units";
        final String DAYS_PARAM = "cnt";

        return Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, zipCode)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, "" + numDays)
                .build().toString();
    }

    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException {

        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONArray weatherArray = new JSONObject(forecastJsonStr).getJSONArray(OWM_LIST);

        Time dayTime = new Time();
        dayTime.setToNow();

        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] result = new String[numDays];

        for (int i = 0; i < weatherArray.length(); i++) {

            String day;
            String description;
            String highAndLow;

            JSONObject dayForecast = weatherArray.getJSONObject(i);

            long dateTime;

            dateTime = dayTime.setJulianDay(julianStartDay + i);
            day = getReadableDateString(dateTime);

            description = dayForecast
                    .getJSONArray(OWM_WEATHER)
                    .getJSONObject(0)
                    .getString(OWM_DESCRIPTION);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);

            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = Math.round(high) + "/" + Math.round(low);

            result[i] = day + " - " + description + " - " + highAndLow;
        }

        return result;
    }

    private String getReadableDateString(long dateTime) {

        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");

        return shortenedDateFormat.format(dateTime);
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader bReader = null;

            String forecastJsonStr = null;

            try {
                URL url = new URL(createUrlString(params[0]));

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream is = urlConnection.getInputStream();
                StringBuilder sb = new StringBuilder();

                if (is != null) {


                    bReader = new BufferedReader(new InputStreamReader(is));

                    String line;

                    while ((line = bReader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    if (sb.length() != 0) {
                        forecastJsonStr = sb.toString();
                    }
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(getClass().getName(), "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bReader != null) {
                    try {
                        bReader.close();
                    } catch (IOException e) {
                        Log.d(getClass().getName(), "Error ", e);
                    }
                }
            }
            return forecastJsonStr;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                weekForecast.clear();

                weekForecast.addAll(Arrays.asList(getWeatherDataFromJson(s, 7)));

                mForecastAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                Log.e("ForecastFragment", "Error getting weather", e);
            }
        }
    }
}
