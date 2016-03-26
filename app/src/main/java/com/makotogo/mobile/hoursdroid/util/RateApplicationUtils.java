package com.makotogo.mobile.hoursdroid.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makotogo.mobile.hoursdroid.R;


public class RateApplicationUtils {

    public static final String PREF_KEY_RATE_APPLICATION = "pref_key_rateApplication";
    public static final String PREF_KEY_LAUNCH_COUNT = "pref_key_launchCount";
    public static final String PREF_KEY_DATE_OF_FIRST_LAUNCH = "pref_key_dateOfFirstLaunch";
    public static final String PREF_KEY_DO_NOT_REMIND_AGAIN = "pref_key_doNotRemindAgain";
    public static final int NUMBER_OF_DAYS_TIL_PROMPT = 7;
    public static final int NUMBER_OF_LAUNCHES_TIL_PROMPT = 10;
    private static final String TAG = RateApplicationUtils.class.getName();
//  public static final int NUMBER_OF_DAYS_TIL_PROMPT = 0;// TESTING VALUE
//  public static final int NUMBER_OF_LAUNCHES_TIL_PROMPT = 0;// TESTING VALUE

    public static void handleApplicationLaunch(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean doNotRemindAgain = sharedPrefs.getBoolean(PREF_KEY_DO_NOT_REMIND_AGAIN, false);
        if (!doNotRemindAgain) {
            // Increment launch counter
            long launchCount = sharedPrefs.getLong(PREF_KEY_LAUNCH_COUNT, 0);
            launchCount++;
            Log.d(TAG, "The application has been launched " + launchCount + " times.");
            sharedPrefs.edit().putLong(PREF_KEY_LAUNCH_COUNT, launchCount).commit();

            // Get date of first launch
            long now = System.currentTimeMillis();
            Long dateOfFirstLaunch = sharedPrefs.getLong(PREF_KEY_DATE_OF_FIRST_LAUNCH, now);
            if (dateOfFirstLaunch == now) {
                sharedPrefs.edit().putLong(PREF_KEY_DATE_OF_FIRST_LAUNCH, now).commit();
            }
            long numberOfDaysSinceFirstLaunch = convertMillisToDays(now - dateOfFirstLaunch);
            Log.d(TAG, "It has been " + numberOfDaysSinceFirstLaunch + " days since first launch");

            if (numberOfDaysSinceFirstLaunch >= NUMBER_OF_DAYS_TIL_PROMPT &&
                    launchCount >= NUMBER_OF_LAUNCHES_TIL_PROMPT) {
                // It's time. Ask the user to rate the app.
                showRateApplicationDialog(context, sharedPrefs);
            }
        }
    }

    private static void showRateApplicationDialog(final Context context, final SharedPreferences sharedPrefs) {
        final Dialog dialog = new Dialog(context);
        dialog.setTitle(R.string.rate_application_title);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(context);
        textView.setText(R.string.rate_application_ramble);
        textView.setWidth(240);
        textView.setPadding(4, 0, 4, 10);
        layout.addView(textView);

        Button rateButton = new Button(context);
        rateButton.setText(R.string.rate_application_rate_button);
        rateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlayStoreListing(context);
                sharedPrefs.edit().putBoolean(PREF_KEY_DO_NOT_REMIND_AGAIN, true).commit();
                dialog.dismiss();
            }

        });
        layout.addView(rateButton);

        Button remindLaterButton = new Button(context);
        remindLaterButton.setText(R.string.rate_application_remind_me_later);
        remindLaterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Remind me later...");
                dialog.dismiss();
            }
        });
        layout.addView(remindLaterButton);

        Button doNotAskAgainButton = new Button(context);
        doNotAskAgainButton.setText(R.string.rate_application_do_not_ask_again);
        doNotAskAgainButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Do not ask again...");
                sharedPrefs.edit().putBoolean(PREF_KEY_DO_NOT_REMIND_AGAIN, true).commit();
                dialog.dismiss();
            }
        });
        layout.addView(doNotAskAgainButton);

        dialog.setContentView(layout);
        dialog.show();
    }

    public static void goToPlayStoreListing(final Context context) {
        String packageName = context.getPackageName();
        String marketUri = context.getResources().getString(R.string.market_uri_prefix);
        Log.d(TAG, "Requesting a rating from URI => " + marketUri + packageName);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(marketUri + packageName));
        context.startActivity(intent);
    }

    private static long convertMillisToDays(long valueToConvert) {
        long ret = 0;
        //
        if (valueToConvert > 0) {
            ret = valueToConvert / 1000 // seconds
                    / 60 // seconds per minute
                    / 60 // minutes per hour
                    / 24 // hours per day
            ;
        }
        return ret;
    }

}
