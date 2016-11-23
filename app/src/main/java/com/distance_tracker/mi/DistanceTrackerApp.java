package com.distance_tracker.mi;

import android.app.Application;

/**
 * Custom application class so we can store variables throughout the lifetime of the app.
 */
public class DistanceTrackerApp extends Application {

    // Prompt the user only once per application session to approve Location Tracking or to enable GPS
    private boolean mUserApprovedLocationTracking = false;
    private boolean mPromptEnableGpsShown = false;

    public boolean hasUserApprovedLocationTracking() {
        return mUserApprovedLocationTracking;
    }

    public void setUserApprovedLocationTracking(boolean isApproved) {
        mUserApprovedLocationTracking = isApproved;
    }

    public boolean hasPromptToEnableGpsBeenShown() {
        return mPromptEnableGpsShown;
    }

    public void setPromptToEnableGpsBeenShown(boolean promptShown) {
        mPromptEnableGpsShown = promptShown;
    }
}
