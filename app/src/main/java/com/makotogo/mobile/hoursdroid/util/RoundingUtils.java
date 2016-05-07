package com.makotogo.mobile.hoursdroid.util;

import android.content.Context;
import android.util.Log;

import org.joda.time.LocalDateTime;

import java.util.Date;

/**
 * Created by sperry on 5/4/16.
 */
public class RoundingUtils {
    private static final String TAG = RoundingUtils.class.getSimpleName();

    /**
     * Apply any rounding to the specified total (in millis) if necessary. That is,
     * if rounding is specified in the System Options.
     *
     * @param context - The context (used for making system calls)
     * @param total   The total to round (in millis)
     * @return long - the rounded (if necessary) total
     */
    public static long applyRoundingIfNecessary(Context context, long total) {
        Log.d(TAG, "Total is " + total + " ms...");
        int roundingBounary = ApplicationOptions.instance(context).getRounding();
        Log.d(TAG, "Rounding to nearest " + roundingBounary + " minute...");
        if (roundingBounary > 0) {
            // Rounding millis is rounding boundary in minutes * 60 s/min * 1000 ms/s
            long roundingMillis = roundingBounary * 60 * 1000;
            // Round up to the nearest interval by subtracting the remainder from the rounding millis
            long roundingAmount = roundingMillis - (total % roundingMillis);
            if (roundingAmount < roundingMillis) {
                Log.d(TAG, "Adding " + roundingAmount + " ms...");
                total += roundingAmount;
            }
            Log.d(TAG, "Total is now (after rounding) " + total);
        }
        return total;
    }

    /**
     * Calculates a new Date object, and rounds down the seconds and milliseconds to zero
     * to give a nice new value at the minute boundary.
     *
     * @return
     */
    public static Date calculateDateWithRounding() {
        final String METHOD = "calculateDateWithRounding()";
        LocalDateTime localDateTime = new LocalDateTime().withSecondOfMinute(0).withMillisOfSecond(0);
        Date ret = localDateTime.toDate();
        Log.d(TAG, METHOD + "Returning new date: " + ret);
        return ret;
    }

}
