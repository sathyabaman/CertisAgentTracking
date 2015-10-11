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

import lk.agent.certislanka.certisagenttracking.model.Schedule;


/**
 * Created by administrator on 7/6/15.
 */
public class ScheduleDAO {

    public static final String TAG = "ScheduleDAO";


    // Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {
            DBHelper.COLUMN_SCHEDULE_ID,
            DBHelper.COLUMN_SCHEDULE_NAME,
            DBHelper.COLUMN_SCHEDULE_DATE   };

    public ScheduleDAO(Context context) {
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

    public Schedule createschedule(int id, String name, String date) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_SCHEDULE_ID, id);
        values.put(DBHelper.COLUMN_SCHEDULE_NAME, name);
        values.put(DBHelper.COLUMN_SCHEDULE_DATE, date);
        long insertId = mDatabase
                .insert(DBHelper.TABLE_SCHEDULE, null, values);
        Cursor cursor = mDatabase.query(DBHelper.TABLE_SCHEDULE, mAllColumns,
                DBHelper.COLUMN_SCHEDULE_ID + " = " + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        Schedule newschedule = cursorToSchedule(cursor);
        cursor.close();
        return newschedule;
    }


    public void deleteSchedule(Schedule schedule) {
        int id = schedule.getId();
        mDatabase.delete(DBHelper.TABLE_SCHEDULE, DBHelper.COLUMN_SCHEDULE_ID + " = " + id, null);
    }

    public void deleteAllSchedule() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_SCHEDULE, null, null);
    }



    public int get_schedule_count() {
        int schedule_count = 0;
        Cursor cursor = mDatabase.query(DBHelper.TABLE_SCHEDULE, mAllColumns,
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Schedule schedule = cursorToSchedule(cursor);
            schedule_count =  cursor.getCount();

        }

        return schedule_count;
    }


    public List<Schedule> getAllschedules() {
        List<Schedule> listSchedules = new ArrayList<Schedule>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_SCHEDULE, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Schedule schedule = cursorToSchedule(cursor);
                listSchedules.add(schedule);
                cursor.moveToNext();
            }

            // make sure to close the cursor
            cursor.close();
        }
        return listSchedules;
    }

    public Schedule getScheduleById(int id) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_SCHEDULE, mAllColumns,
                DBHelper.COLUMN_SCHEDULE_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Schedule schedule = cursorToSchedule(cursor);
        return schedule;
    }

    protected Schedule cursorToSchedule(Cursor cursor) {
        Schedule schedule = new Schedule();
        schedule.setId(cursor.getInt(0));
        schedule.setName(cursor.getString(1));
        schedule.setDate(cursor.getString(2));

        return schedule;
    }


}
