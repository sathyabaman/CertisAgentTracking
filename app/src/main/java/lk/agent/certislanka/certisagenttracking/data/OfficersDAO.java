package lk.agent.certislanka.certisagenttracking.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lk.agent.certislanka.certisagenttracking.model.Officers;

import static lk.agent.certislanka.certisagenttracking.data.DBHelper.TABLE_FIELD_OFFICERS;

/**
 * Created by administrator on 7/27/15.
 */
public class OfficersDAO {

    public static final String TAG = "officers";

    // Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;
    private Context mContext;


    private String[] mAllColumns = {
            DBHelper.COLUMN_FIELD_ID,
            DBHelper.COLUMN_VISIT_ID,
            DBHelper.COLUMN_ITEM_CODE,
            DBHelper.COLUMN_ITEM_COUNT};


    public OfficersDAO(Context context) {
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


    public Officers createofficers(String off_visit_id, String off_itm_code, String off_itm_count) {

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_VISIT_ID, off_visit_id);
        values.put(DBHelper.COLUMN_ITEM_CODE, off_itm_code);
        values.put(DBHelper.COLUMN_ITEM_COUNT, off_itm_count);
        long insertId = mDatabase
                .insert(TABLE_FIELD_OFFICERS, null, values);
        Cursor cursor = mDatabase.query(TABLE_FIELD_OFFICERS, mAllColumns, null, null, null, null, null);

        Officers newofficers =    null;
        if (cursor != null&& cursor.moveToFirst()) {
            cursor.moveToFirst();
            newofficers = cursorTofieldofficers(cursor);
        }

        cursor.close();
        return newofficers;
    }

    public List<Officers> getAllOfficersforView(int id) {
        List<Officers> listOfficers = new ArrayList<Officers>();

        String where = "off_visit_id=? ";
        String[] args = {String.valueOf(id)};
        Cursor cursor = mDatabase.query(TABLE_FIELD_OFFICERS, mAllColumns, where, args, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Officers officers = cursorTofieldofficers(cursor);
                listOfficers.add(officers);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        }
        return listOfficers;
    }

    protected Officers cursorTofieldofficers(Cursor cursor) {
        Officers officers = new Officers();
        officers.setOff_visit_id(cursor.getString(1));
        officers.setOff_itm_code(cursor.getString(2));
        officers.setOff_itm_count(cursor.getString(3));
        return officers;
    }


    public void deleteAllofficers() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TABLE_FIELD_OFFICERS, null, null);
    }


}
