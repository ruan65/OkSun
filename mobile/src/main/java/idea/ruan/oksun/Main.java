package idea.ruan.oksun;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.fitness.request.DisableFitRequest;

import idea.ruan.oksun.data.WeatherContract;

public class Main extends AppCompatActivity implements ForecastFragment.Callback {

    private String mLocation;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        mLocation = Utility.getPreferredLocation(this);

        if (findViewById(R.id.weather_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container,
                                new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else mTwoPane = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        String currentLocation = Utility.getPreferredLocation(this);

        if (mLocation != null && !mLocation.equals(currentLocation)) {

            mLocation = currentLocation;

            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_forecast);

            if (null != ff) ff.onLocationChanged();

            DetailFragment df = (DetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(DETAILFRAGMENT_TAG);

            if (df != null) df.onLocationChanged(mLocation);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_view_location:

                openPreferredLocationInMap();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {

        Intent intent = new Intent(Intent.ACTION_VIEW);

        String location = Utility.getPreferredLocation(this);

//        Uri geoLocation = Uri.parse("geo:" + currentLocationCoords + "?z=11");

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        intent.setData(geoLocation);

        ComponentName componentName = intent.resolveActivity(getPackageManager());

        if (componentName != null) {
            startActivity(intent);
        } else {
            Log.d(Main.class.getSimpleName(),
                    "Couldn't call " + location + ", no receiving apps installed!");
        }
    }

    @Override
    public void onItemSelected(Uri uri) {

        if (mTwoPane) {

            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, uri);

            DetailFragment df = new DetailFragment();

            df.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, df)
                    .commit();

        } else {

            Intent intent = new Intent(this, DetailActivity.class).setData(uri);

            startActivity(intent);
        }
    }
}
