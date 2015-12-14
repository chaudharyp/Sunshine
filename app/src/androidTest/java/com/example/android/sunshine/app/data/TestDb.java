/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for location table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                   locationColumnHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.WeatherEntry.TABLE_NAME + ")", null);
        assertTrue("Error: Unable to query database for weather table information", c.moveToFirst());

        final HashSet<String> weatherColumnHashSet = new HashSet<>();
        weatherColumnHashSet.add(WeatherContract.WeatherEntry._ID);
        weatherColumnHashSet.add(WeatherContract.WeatherEntry.COLUMN_DATE);
        weatherColumnHashSet.add(WeatherContract.WeatherEntry.COLUMN_DEGREES);
        weatherColumnHashSet.add(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
        weatherColumnHashSet.add(WeatherContract.WeatherEntry.COLUMN_LOC_KEY);
        weatherColumnHashSet.add(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        weatherColumnHashSet.add(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        weatherColumnHashSet.add(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
        weatherColumnHashSet.add(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
        weatherColumnHashSet.add(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
        weatherColumnHashSet.add(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            weatherColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: Data returned doesn't contain all Weather table columns", weatherColumnHashSet.isEmpty());

        db.close();
    }

    public void testLocationTable() {
        // Get reference to writable database
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();

        ContentValues rowToInsert = TestUtilities.createNorthPoleLocationValues();

        // Insert ContentValues into database and get a row ID back
        long locationRowId = TestUtilities.insertNorthPoleLocationValues(mContext);
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        // Query the database and receive a Cursor back
        Cursor locationRowCursor = db.query(WeatherContract.LocationEntry.TABLE_NAME, null, null, null, null, null, null);

        // Move the cursor to a valid database row
        assertTrue("Error: No row found in location table", locationRowCursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error: Cursor returned doesn't match with ContentValues inserted", locationRowCursor, rowToInsert);

        // Finally, close the cursor and database
        locationRowCursor.close();
        db.close();
    }

    public void testWeatherTable() {
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();
        long locationRowId = TestUtilities.insertNorthPoleLocationValues(mContext);

        assertTrue("Error: Cannot insert row into location table", locationRowId != -1);

        ContentValues weatherRowValues = TestUtilities.createWeatherValues(locationRowId);

        long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherRowValues);

        assertTrue("Error: Cannot insert row into weather table", weatherRowId != -1);

        Cursor weatherRowCursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME, null, null, null, null, null, null);

        assertTrue("Error: No row found in weather table", weatherRowCursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Weather cursor returned doesn't match with ContentValues inserted", weatherRowCursor, weatherRowValues);

        weatherRowCursor.close();
        db.close();
    }

    public long insertLocation() {
        return -1L;
    }
}
