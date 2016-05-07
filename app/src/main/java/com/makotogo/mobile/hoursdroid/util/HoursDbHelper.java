package com.makotogo.mobile.hoursdroid.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.makotogo.mobile.hoursdroid.model.HoursDbSchema;

/**
 * Created by sperry on 1/3/16.
 */
public class HoursDbHelper extends SQLiteOpenHelper {

    private static final String TAG = HoursDbHelper.class.getSimpleName();

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "hoursdroid.db";

    public HoursDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating table - " + HoursDbSchema.JobTable.NAME + "...");
        db.execSQL(HoursDbSchema.JobTable.CREATE_SQL);
        Log.d(TAG, "Creating table - " + HoursDbSchema.ProjectTable.NAME + "...");
        db.execSQL(HoursDbSchema.ProjectTable.CREATE_SQL);
        Log.d(TAG, "Creating table - " + HoursDbSchema.HoursTable.NAME + "...");
        db.execSQL(HoursDbSchema.HoursTable.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
