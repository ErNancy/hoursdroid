package com.makotogo.mobile.hoursdroid.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.makotogo.mobile.hoursdroid.model.HoursDbSchema;

/**
 * Created by sperry on 1/3/16.
 */
public class HoursDbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "hoursdroid.db";

    public HoursDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HoursDbSchema.JobTable.CREATE_SQL);
        db.execSQL(HoursDbSchema.ProjectTable.CREATE_SQL);
        db.execSQL(HoursDbSchema.TimeRecordTable.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
