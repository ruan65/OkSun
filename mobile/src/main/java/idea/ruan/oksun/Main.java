package idea.ruan.oksun;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
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
}
