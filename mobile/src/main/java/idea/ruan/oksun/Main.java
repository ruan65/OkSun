package idea.ruan.oksun;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

                Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.setData(Uri.parse("geo:" +
                        PreferenceManager.getDefaultSharedPreferences(this)
                                .getString(getString(R.string.current_location), "") + "?z=11"));

                ComponentName componentName = intent.resolveActivity(getPackageManager());

                if (componentName != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, getString(R.string.toast_err_location_view),
                            Toast.LENGTH_LONG).show();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
