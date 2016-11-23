package com.distance_tracker.mi.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.distance_tracker.mi.Calculation;
import com.distance_tracker.mi.storage.DataManager;

/**
 * Local Service to track GPS Location
 */
public class GPSTrackerService extends Service {
    private LocationManager mLocationManager;
    private boolean mInitialLocationSet = false;
    private Context mContext = null;

    private static final String TAG = "GPSTrackerService";
    private static final int LOCATION_INTERVAL = 10000;     // 10 seconds = 10000 milliseconds
    private static final float LOCATION_DISTANCE = 0;

    private class LocationListener implements android.location.LocationListener {

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);

            // determine the distance between the last recorded location and the new location
            double newDistance = distanceInMeters(DataManager.getCurrentLocation(mContext), location);

            if (mInitialLocationSet)
                DataManager.setCurrentLocation(mContext, location);
            else {
                mInitialLocationSet = true;
                DataManager.setInitialLocation(mContext, location);
            }

            // add new distance to last distance
            double accumulatedDistanceInMeters = DataManager.getAccumulatedDistanceInMeters(mContext);
            DataManager.setAccumulatedDistanceInMeters(mContext, accumulatedDistanceInMeters + newDistance);

            // Post the event
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("onLocationChanged");
            sendBroadcast(broadcastIntent);
         }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    /**
     * Class for clients to access.
     */
    public class LocalBinder extends Binder {
        public GPSTrackerService getService() {
            return GPSTrackerService.this;
        }
    }

    @Override
    public void onCreate() {
        try {
            mContext = this;
            mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            if (DataManager.getIsTracking(this.getApplicationContext()))
                continueTracking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Determine if GPS is enabled
     */
    public boolean isGpsEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Register for location updates
     */
    public void startTracking() {
        mInitialLocationSet = false;
        DataManager.setIsTracking(this, true);
        DataManager.setAccumulatedDistanceInMeters(this, 0d);

        Location location = new Location("");//provider name is unecessary
        location.setLatitude(0d);
        location.setLongitude(0d);
        location.setTime(0l);

        // reset cached locations
        DataManager.setInitialLocation(this, location);
        DataManager.setCurrentLocation(this, location);

        continueTracking();
    }

    private void continueTracking() {
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    /**
     * Removes all location updates for the specified LocationListener.
     */
    public void stopTracking() {
        DataManager.setIsTracking(this, false);

        try {
            mLocationManager.removeUpdates(mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to remove location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        try {
            mLocationManager.removeUpdates(mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to remove location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    private double distanceInMeters(Location startLocation, Location endLocation) {

        if (startLocation.getLatitude() == 0d || startLocation.getLongitude() == 0d)
            return 0d;

        if (endLocation.getLatitude() == 0d || endLocation.getLongitude() == 0d)
            return 0d;

        return startLocation.distanceTo(endLocation);
    }
}