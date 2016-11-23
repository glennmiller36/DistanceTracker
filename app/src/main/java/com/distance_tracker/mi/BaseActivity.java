package com.distance_tracker.mi;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.distance_tracker.mi.service.GPSTrackerService;

/**
 * Base activity to hide plumbing for GPS Tracking
 */

public class BaseActivity extends AppCompatActivity {

    protected GPSTrackerService mBoundService;
    protected boolean mIsBound;
    private IntentFilter mIntentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntentFilter.addAction("onLocationChanged");
    }


    protected ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been established
            mBoundService = ((GPSTrackerService.LocalBinder)service).getService();

            onServiceConnectedEvent();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected
            mBoundService = null;
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("onLocationChanged")) {
                onLocationChangedEvent();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    /**
     * Called once Service has been successfully connected.
     */
    public void onServiceConnectedEvent() {
        // do nothing in base class
    }

    /**
     * Method overrideable by derived class.
     */
    public void onLocationChangedEvent() {
        // do nothing in base class
    }
}