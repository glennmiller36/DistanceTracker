package com.distance_tracker.mi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.distance_tracker.mi.service.GPSTrackerService;
import com.distance_tracker.mi.storage.DataManager;

/**
 * Initial screen - allow user to Start / Stop Location tracking or view Distance and Location.
 */
public class MainActivity extends BaseActivity {

    private Button mButtonStartStop;
    private Button mButtonDistance;
    private Button mButtonLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mButtonStartStop = (Button) findViewById((R.id.buttonStartStop));
        mButtonDistance = (Button) findViewById((R.id.buttonDistance));
        mButtonLocation = (Button) findViewById((R.id.buttonLocation));
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Service should remain running even if no clients are bound;
        Intent intent = new Intent(this, GPSTrackerService.class);
        startService(intent);

        if (!mIsBound) {
            bindService(intent, mConnection, BIND_AUTO_CREATE);
        }
    }

    /**
     * Called when the activity is no longer visible to the user,
     * because another activity has been resumed and is covering this one.
     */
    @Override
    protected void onStop() {
        super.onStop();

        doUnbindService();
    }

    /*
    * Start / Stop Tracking button click
    */
    public void onClickStartStop(View view) {
        if (DataManager.getIsTracking(this)) {
            mBoundService.stopTracking();
            setupStartTrackingUI();
        }
        else {
            checkTrackLocationApproval();
        }
    }

    /*
    * Distance button click
    */
    public void onClickDistance(View view) {
        Intent intent = new Intent(this, DistanceActivity.class);
        startActivity(intent);
    }

    /*
    * Location button click
    */
    public void onClickLocation(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    /**
     * Verify we've got user approval to track location
     */
    private void checkTrackLocationApproval() {
        if (!DataManager.hasUserApprovedLocationTracking(this.getApplicationContext()))
            approvalToTrackLocation();
        else
            checkGPSEnabled();
    }

    /**
     * If GPS disabled, prompt user once per session to enable via Settings.
     */
    private void checkGPSEnabled() {
        if (!DataManager.hasPromptToEnableGpsBeenShown(this.getApplicationContext()) && !mBoundService.isGpsEnabled()) {
            DataManager.setPromptToEnableGpsBeenShown(this.getApplicationContext(), true);
            showSettingsAlert();
        }
        else
            startTracking();
    }

    /**
     * Enable location tracking
     */
    private void startTracking() {
        mBoundService.startTracking();
        setupStopTrackingUI();
    }

    /**
     * Initialize UI for Start Tracking
     */
    private void setupStartTrackingUI() {
        mButtonStartStop.setText(R.string.start_tracking);
        mButtonStartStop.setTextColor(Color.BLACK);
        mButtonStartStop.setBackgroundColor(Color.GREEN);
    }

    /**
     * Initialize UI for Stop Tracking
     */
    private void setupStopTrackingUI() {
        mButtonStartStop.setText(R.string.stop_tracking);
        mButtonStartStop.setTextColor(Color.WHITE);
        mButtonStartStop.setBackgroundColor(Color.RED);
    }

    /**
     * Prompt user if it's OK to track their location.
     */
    private void approvalToTrackLocation() {
        final Context context = this.getApplicationContext();

        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.app_name)
            .setMessage(R.string.approve_tracking)
            .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DataManager.setUserApprovedLocationTracking(context, true);
                    dialog.cancel();
                    checkGPSEnabled();
                }
            })
            .setNegativeButton(R.string.dont_allow, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .show();
    }

    /**
     * If GPS is not enabled, prompt user to launch Settings Options.
     */
    public void showSettingsAlert(){
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.location_accuracy_tip)
            .setMessage(R.string.location_settings)
            .setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            })
            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    startTracking();
                }
            })
            .show();
    }

    /**
     * On back press to exit the app, prompt the user if they wish to stop tracking distance.
     */
    @Override
    public void onBackPressed() {
        final Context context = this.getApplicationContext();

        if (DataManager.getIsTracking(this)) {
            new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.title_exit_app)
                .setMessage(R.string.exit_app_stoptracking)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mBoundService != null)
                            mBoundService.stopTracking();
                        stopService(new Intent(context, GPSTrackerService.class));
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopService(new Intent(context, GPSTrackerService.class));
                        finish();
                    }
                })
                .show();
        }
        else {
            stopService(new Intent(context, GPSTrackerService.class));
            finish();
        }
    }

    /**
     * Called once Service has been successfully connected
     */
    @Override
    public void onServiceConnectedEvent() {
        mButtonStartStop.setVisibility(View.VISIBLE);
        mButtonDistance.setVisibility(View.VISIBLE);
        mButtonLocation.setVisibility(View.VISIBLE);

        // if rotating device or launching after a crash, check local storage to see if we are in the midst of tracking
        if (DataManager.getIsTracking(this)) {
            setupStopTrackingUI();
        }
        else
            setupStartTrackingUI();
    }
}
