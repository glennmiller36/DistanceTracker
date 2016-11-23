package com.distance_tracker.mi;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.distance_tracker.mi.storage.DataManager;

/**
 * Display current tracked distance
 */
public class DistanceActivity extends BaseActivity {

    private static final String sDATEFORMAT = "E, MMM d h:mm:ss a";

    private TextView mTextStarted;
    private TextView mTextCurrent;
    private TextView mTextDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mTextStarted = (TextView) findViewById(R.id.textStarted);
        mTextCurrent = (TextView) findViewById(R.id.textCurrent);
        mTextDistance = (TextView) findViewById(R.id.textDistance);
    }

    @Override
    protected void onStart() {
        super.onStart();

        refreshUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Location has changed
     */
    public void onLocationChangedEvent() {
        refreshUI();
    }

    private void refreshUI() {

        Location initialLocation = DataManager.getInitialLocation(this.getApplicationContext());
        Location currentLocation = DataManager.getCurrentLocation(this.getApplicationContext());

        mTextStarted.setText(dateToString(initialLocation.getTime()));
        mTextCurrent.setText(dateToString(currentLocation.getTime()));

        String distanceText = Calculation.formatDistance(DataManager.getAccumulatedDistanceInMeters(this.getApplicationContext()), this.getString(R.string.unit_abbreviation));
        mTextDistance.setText(distanceText);
    }

    /**
     * Format valid date.
     */
    public String dateToString(long time) {
        Date date = new Date(time);
        if (date == null || time == 0)
            return this.getString(R.string.unknown);

        SimpleDateFormat dateFormat = new SimpleDateFormat(sDATEFORMAT, Locale.US);
        return dateFormat.format(date);
    }
}
