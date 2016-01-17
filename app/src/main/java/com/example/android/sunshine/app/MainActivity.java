package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String LOG_TAG = MainActivity.class.getSimpleName();
    private String mLocation;
    private final String FORECASTFRAGMENT_TAG = "Forecast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "inside onCreate");
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                                       .commit();
        }
        mLocation = Utility.getPreferredLocation(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "inside onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(this))) {
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            if (ff != null) {
                ff.onLocationChanged();
            }
            mLocation = Utility.getPreferredLocation(this);
        }
        Log.d(LOG_TAG, "inside onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "inside onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "inside onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "inside onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id== R.id.action_location) {
            showLocationOnMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLocationOnMap(){
        String locationSetting = Utility.getPreferredLocation(this);
        Uri locationUri = Uri.parse("geo:0,0")
                             .buildUpon()
                             .appendQueryParameter("q", locationSetting)
                             .build();
        Intent intent = new Intent(Intent.ACTION_VIEW, locationUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "No app found to open location", Toast.LENGTH_SHORT)
                 .show();
        }
    }

}
