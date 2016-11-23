package com.distance_tracker.mi;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.distance_tracker.mi.storage.DataManager;

/**
 * Display a map of your current location
 */
public class LocationActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView mTextLocationDescription;

    private static final String sDATEFORMAT = "E, MMM d, yyyy 'at' h:mm:ss a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mTextLocationDescription = (TextView) findViewById(R.id.textLocationDescription);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // add current location
        addLocationMarker();
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
        addLocationMarker();
    }

    private void addLocationMarker() {
        String locationDescription = this.getString(R.string.current_location_unknown);

        if (mMap != null) {
            mMap.clear();   // clear any existing markers

            Location newLocation = DataManager.getCurrentLocation(this.getApplicationContext());
            if (newLocation.getLatitude() != 0d && newLocation.getLongitude() != 0d) {
                LatLng currentLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentLatLng));

                //Move the camera to the user's location and zoom in
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()), 12.0f));

                locationDescription = this.getString(R.string.last_location_timestamp) + dateToString(newLocation.getTime());
            }
        }

        mTextLocationDescription.setText(locationDescription);
    }

    /**
     * Format valid date.
     */
    public String dateToString(long time) {
        Date date = new Date(time);
        if (date == null || time == 0)
            return null;

        SimpleDateFormat dateFormat = new SimpleDateFormat(sDATEFORMAT, Locale.US);
        return dateFormat.format(date);
    }
}
