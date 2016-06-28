/*
 *     Copyright 2016 Makoto Consulting Group, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

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
