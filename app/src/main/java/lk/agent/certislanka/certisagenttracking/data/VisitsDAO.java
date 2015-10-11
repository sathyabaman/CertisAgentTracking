package lk.agent.certislanka.certisagenttracking.data;

/**
 * Created by administrator on 7/7/15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lk.agent.certislanka.certisagenttracking.model.Visits;


/**
 * Created by administrator on 7/6/15.
 */
public class VisitsDAO {

    public static final String TAG = "visitsDAO";


    // Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {
            DBHelper.COLUMN_VISITS_ID,
            DBHelper.COLUMN_VISITS_SCHEDULE_ID,
            DBHelper.COLUMN_VISITS_NAME,
            DBHelper.COLUMN_VISITS_TIME,
            DBHelper.COLUMN_VISITS_PLACE,
            DBHelper.COLUMN_VISITS_ADDRESS,
            DBHelper.COLUMN_VISITS_TEL,
            DBHelper.COLUMN_VISITS_LOCATION_LAT,
            DBHelper.COLUMN_VISITS_LOCATION_LNG,
            DBHelper.COLUMN_VISITS_STATUS  };

    public VisitsDAO(Context context) {
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

    public Visits createvisits(int vid, int v_sid, String vname, String vtime, String vplace, String address, String telephone, String vlat, String vlong, int vstatus) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_VISITS_ID, vid);
        values.put(DBHelper.COLUMN_VISITS_SCHEDULE_ID, v_sid);
        values.put(DBHelper.COLUMN_VISITS_NAME, vname);
        values.put(DBHelper.COLUMN_VISITS_TIME, vtime);
        values.put(DBHelper.COLUMN_VISITS_PLACE, vplace);
        values.put(DBHelper.COLUMN_VISITS_ADDRESS, address);
        values.put(DBHelper.COLUMN_VISITS_TEL, telephone);
        values.put(DBHelper.COLUMN_VISITS_LOCATION_LAT, vlat);
        values.put(DBHelper.COLUMN_VISITS_LOCATION_LNG, vlong);
        values.put(DBHelper.COLUMN_VISITS_STATUS, vstatus);
        long insertId = mDatabase
                .insert(DBHelper.TABLE_VISITS, null, values);
        Cursor cursor = mDatabase.query(DBHelper.TABLE_VISITS, mAllColumns,
                DBHelper.COLUMN_VISITS_ID + " = " + insertId, null, null,
                null, null);

        Visits newvisits = null;
        if (cursor != null&& cursor.moveToFirst()) {
            cursor.moveToFirst();
            newvisits = cursorTovisits(cursor);

        }
        cursor.close();
        return newvisits;
    }



    public List<Visits> getAllvisits() {
        List<Visits> listVisits = new ArrayList<Visits>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_VISITS, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Visits visits = cursorTovisits(cursor);
                listVisits.add(visits);
                cursor.moveToNext();
            }

            // make sure to close the cursor
            cursor.close();
        }
        return listVisits;
    }

    public List<Visits> getAllvisitsforSchedule(int id) {
        List<Visits> listVisits = new ArrayList<Visits>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_VISITS, mAllColumns,
                DBHelper.COLUMN_VISITS_SCHEDULE_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, "visit_time ASC");
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Visits visits = cursorTovisits(cursor);
                listVisits.add(visits);
                cursor.moveToNext();
            }

            // make sure to close the cursor

            cursor.close();
        }
        return listVisits;
    }

    public Visits getvisitById(int id) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_VISITS, mAllColumns,
                DBHelper.COLUMN_VISITS_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Visits visits = cursorTovisits(cursor);
        return visits;
    }

    protected Visits cursorTovisits(Cursor cursor) {
        Visits schedule = new Visits();

        schedule.setvId(cursor.getInt(0));
        schedule.setVschID(cursor.getInt(1));
        schedule.setVname(cursor.getString(2));
        schedule.setVtime(cursor.getString(3));
        schedule.setVplace(cursor.getString(4));
        schedule.setVaddress(cursor.getString(5));
        schedule.setvtelephone(cursor.getString(6));
        schedule.setVlat(cursor.getString(7));
        schedule.setVlong(cursor.getString(8));
        schedule.setVstatus(cursor.getInt(9));

        return schedule;

    }


    public void deleteVisits(Visits visits) {
        int id = visits.getvId();
        mDatabase.delete(DBHelper.TABLE_VISITS, DBHelper.COLUMN_VISITS_ID + " = " + id, null);
    }

    public void deleteAllVisits() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_VISITS, null, null);
    }


}
