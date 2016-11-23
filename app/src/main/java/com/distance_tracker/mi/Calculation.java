package com.distance_tracker.mi;

import android.content.Context;

import com.distance_tracker.mi.storage.DataManager;

/**
 * Created by MillerMac on 11/22/16.
 */

public class Calculation {

    public static String formatDistance(double accumulatedDistanceInMeters, String unit) {

        double distanceInUnits = 0d;

        if (accumulatedDistanceInMeters > 0d) {
            if (unit == "MI")
                distanceInUnits = accumulatedDistanceInMeters / 1609.344;
            else
                distanceInUnits = accumulatedDistanceInMeters * 0.001;
        }

        return String.format("%.1f %s", distanceInUnits, unit);
    }
}
