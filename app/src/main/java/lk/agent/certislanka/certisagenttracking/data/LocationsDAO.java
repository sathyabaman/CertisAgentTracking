package lk.agent.certislanka.certisagenttracking.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lk.agent.certislanka.certisagenttracking.model.Locations;

/**
 * Created by administrator on 7/21/15.
 */
public class LocationsDAO {

    public static final String TAG = "locationdata";

    // Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;

    private Context mContext;


    private String[] mAllColumns = {
            DBHelper.COLUMN_LOC_ID,
            DBHelper.COLUMN_LOC_KEY,
            DBHelper.COLUMN_LOC_LAT,
            DBHelper.COLUMN_LOC_LNG,
            DBHelper.COLUMN_LOC_BATTERY,
            DBHelper.COLUMN_LOC_DATE,
            DBHelper.COLUMN_LOC_GPSKEY};


    public LocationsDAO(Context context) {
        this.mContext = context;
        mDbHelper = new DBHelper(context);
        // open the database
        try {
            open();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException on openning database " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }



    public Locations createlocation(String key, String agent_bg_location_lat, String agent_bg_location_lng, String agent_bg_battery, String agent_bg_app_date, String gps_key) {

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_LOC_KEY, key);
        values.put(DBHelper.COLUMN_LOC_LAT, agent_bg_location_lat);
        values.put(DBHelper.COLUMN_LOC_LNG, agent_bg_location_lng);
        values.put(DBHelper.COLUMN_LOC_BATTERY, agent_bg_battery);
        values.put(DBHelper.COLUMN_LOC_DATE, agent_bg_app_date);
        values.put(DBHelper.COLUMN_LOC_GPSKEY, gps_key);
        long insertId = mDatabase
                .insert(DBHelper.TABLE_LOCATION_DATA, null, values);
        Cursor cursor = mDatabase.query(DBHelper.TABLE_LOCATION_DATA, mAllColumns,
                null, null, null,
                null, null);

        Locations newlocation =    null;
        if (cursor != null&& cursor.moveToFirst()) {
            cursor.moveToFirst();
            newlocation = cursorToLocations(cursor);

        }
        cursor.close();
        return newlocation;
    }


    public List<Locations> getAllLocations() {
        List<Locations> listlocations = new ArrayList<Locations>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_LOCATION_DATA, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Locations locations = cursorToLocations(cursor);
                listlocations.add(locations);
                cursor.moveToNext();
            }

            // make sure to close the cursor
            cursor.close();
        }
        return listlocations;
    }



    public List<Locations> getLocationsToremoteSync() {
        List<Locations> listLocations = new ArrayList<Locations>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_LOCATION_DATA, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Locations locations = cursorToLocations(cursor);
                listLocations.add(locations);
                cursor.moveToNext();
            }

            // make sure to close the cursor

            cursor.close();
        }
        return listLocations;
    }



    public int Locations_get_unsync_count() {
        int item_count = 0;
        Cursor cursor = mDatabase.query(DBHelper.TABLE_LOCATION_DATA, mAllColumns,
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Locations items = cursorToLocations(cursor);
            item_count =  cursor.getCount();

        }


        return item_count;

    }


    protected Locations cursorToLocations(Cursor cursor) {

        Locations laocation = new Locations();
        laocation.setId(cursor.getInt(0));
        laocation.setKey(cursor.getString(1));
        laocation.setAgent_bg_location_lat(cursor.getString(2));
        laocation.setAgent_bg_location_lng(cursor.getString(3));
        laocation.setAgent_bg_battery(cursor.getString(4));
        laocation.setAgent_bg_app_date(cursor.getString(5));
        laocation.setGps_key(cursor.getString(6));
        return laocation;
    }


    public void deleteAllLocationData() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_LOCATION_DATA, null, null);
    }




}
