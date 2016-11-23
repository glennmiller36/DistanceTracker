package com.distance_tracker.mi.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.distance_tracker.mi.DistanceTrackerApp;

/**
 * Class to manage data access
 */
public class DataManager {

    public static final String PREFS_NAME = "DistanceTrackerPrefsFile";
    public static final String IS_TRACKING = "IsTracking";
    public static final String INITIAL_LATITUDE = "InitialLatitude";
    public static final String INITIAL_LONGITUDE = "InitialLongitude";
    public static final String INITIAL_DATETIME = "InitialDateTime";
    public static final String CURRENT_LATITUDE = "CurrentLatitude";
    public static final String CURRENT_LONGITUDE = "CurrentLongitude";
    public static final String CURRENT_DATETIME = "CurrentDateTime";
    public static final String ACCUMULATED_DISTANCE_IN_METERS = "AccumulatedDistanceInMeters";

    /**
     * Shared Preferences
     */

    public static boolean getIsTracking(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(IS_TRACKING, false);
    }

    public static void setIsTracking(Context context, boolean isTracking) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_TRACKING, isTracking);

        // Commit the edits!
        editor.commit();
    }

    public static Location getInitialLocation(Context context) {

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        double latitude = Double.longBitsToDouble(settings.getLong(INITIAL_LATITUDE, 0));
        double longitude = Double.longBitsToDouble(settings.getLong(INITIAL_LONGITUDE, 0));
        long date = settings.getLong(INITIAL_DATETIME, 0);

        Location targetLocation = new Location("");//provider name is unecessary
        targetLocation.setLatitude(latitude);
        targetLocation.setLongitude(longitude);
        targetLocation.setTime(date);

        return targetLocation;
    }

    public static void setInitialLocation(Context context, Location location) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(INITIAL_LATITUDE, Double.doubleToLongBits(location.getLatitude()));
        editor.putLong(INITIAL_LONGITUDE, Double.doubleToLongBits(location.getLongitude()));
        editor.putLong(INITIAL_DATETIME, location.getTime());

        // Commit the edits!
        editor.commit();
    }

    public static Location getCurrentLocation(Context context) {

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        double latitude = Double.longBitsToDouble(settings.getLong(CURRENT_LATITUDE, 0));
        double longitude = Double.longBitsToDouble(settings.getLong(CURRENT_LONGITUDE, 0));
        long date = settings.getLong(CURRENT_DATETIME, 0);

        Location targetLocation = new Location("");//provider name is unecessary
        targetLocation.setLatitude(latitude);
        targetLocation.setLongitude(longitude);
        targetLocation.setTime(date);

        return targetLocation;
    }

    public static void setCurrentLocation(Context context, Location location) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(CURRENT_LATITUDE, Double.doubleToLongBits(location.getLatitude()));
        editor.putLong(CURRENT_LONGITUDE, Double.doubleToLongBits(location.getLongitude()));
        editor.putLong(CURRENT_DATETIME, location.getTime());

        // Commit the edits!
        editor.commit();
    }

    public static double getAccumulatedDistanceInMeters(Context context) {

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        String distanceAsString = settings.getString(ACCUMULATED_DISTANCE_IN_METERS, "0");

        try {
            return Double.parseDouble(distanceAsString);
        } catch (NumberFormatException e) {
            return 0d;
        }
    }

    public static void setAccumulatedDistanceInMeters(Context context, double distanceInMeters) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ACCUMULATED_DISTANCE_IN_METERS, Double.toString(distanceInMeters));

        // Commit the edits!
        editor.commit();
    }

    /**
     * Application global variables
     */

    public static boolean hasUserApprovedLocationTracking(Context context) {
        return ((DistanceTrackerApp) context).hasUserApprovedLocationTracking();
    }

    public static void setUserApprovedLocationTracking(Context context, boolean isApproved) {
        ((DistanceTrackerApp) context).setUserApprovedLocationTracking(isApproved);
    }

    public static boolean hasPromptToEnableGpsBeenShown(Context context) {
        return ((DistanceTrackerApp) context).hasPromptToEnableGpsBeenShown();
    }

    public static void setPromptToEnableGpsBeenShown(Context context, boolean promptShown) {
        ((DistanceTrackerApp) context).setPromptToEnableGpsBeenShown(promptShown);
    }

}
