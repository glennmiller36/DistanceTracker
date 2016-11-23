package com.distance_tracker.mi;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CalculationTest {
    @Test
    public void formatDistance_zero_MI() {
        assertEquals("0.0 MI", Calculation.formatDistance(0, "MI"));
    }

    @Test
    public void formatDistance_10000_MI() {
        assertEquals("6.2 MI", Calculation.formatDistance(10000d, "MI"));
    }

    @Test
    public void formatDistance_zero_KM() {
        assertEquals("0.0 KM", Calculation.formatDistance(0, "KM"));
    }

    @Test
    public void formatDistance_10000_KM() {
        assertEquals("10.0 KM", Calculation.formatDistance(10000d, "KM"));
    }
}