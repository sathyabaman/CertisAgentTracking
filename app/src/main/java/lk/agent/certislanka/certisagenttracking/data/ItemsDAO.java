package lk.agent.certislanka.certisagenttracking.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lk.agent.certislanka.certisagenttracking.model.Items;
import lk.agent.certislanka.certisagenttracking.model.Visits;

import static lk.agent.certislanka.certisagenttracking.data.DBHelper.TABLE_ITEM;

/**
 * Created by administrator on 7/10/15.
 */
public class ItemsDAO {

    public static final String TAG = "itemsDAO";

    // Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;

    private Context mContext;

    private String[] mAllColumns = {
            DBHelper.COLUMN_ITEM_ID,
            DBHelper.COLUMN_ITEM_VISIT_ID,
            DBHelper.COLUMN_ITEM_NAME,
            DBHelper.COLUMN_ITEM_CHECK_STATUS,
            DBHelper.COLUMN_ITEM_COMMENT,
            DBHelper.COLUMN_ITEM_LAT,
            DBHelper.COLUMN_ITEM_LNG,
            DBHelper.COLUMN_ITEM_SUB_TIME,
            DBHelper.COLUMN_ITEM_REMOTE_status};


    public ItemsDAO(Context context) {
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



    public Items createitems(int itm_id, int itm_vst_id, String itm_name, int itm_chk_sts, String itm_commnt, String itm_lat, String itm_lng, String itm_time, int itm_rmo_sts) {

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ITEM_ID, itm_id);
        values.put(DBHelper.COLUMN_ITEM_VISIT_ID, itm_vst_id);
        values.put(DBHelper.COLUMN_ITEM_NAME, itm_name);
        values.put(DBHelper.COLUMN_ITEM_CHECK_STATUS, itm_chk_sts);
        values.put(DBHelper.COLUMN_ITEM_COMMENT, itm_commnt);
        values.put(DBHelper.COLUMN_ITEM_LAT, itm_lat);
        values.put(DBHelper.COLUMN_ITEM_LNG, itm_lng);
        values.put(DBHelper.COLUMN_ITEM_SUB_TIME, itm_time);
        values.put(DBHelper.COLUMN_ITEM_REMOTE_status, itm_rmo_sts);
        long insertId = mDatabase
                .insert(TABLE_ITEM, null, values);
        Cursor cursor = mDatabase.query(TABLE_ITEM, mAllColumns,
                DBHelper.COLUMN_ITEM_ID + " = " + insertId, null, null,
                null, null);

        Items newitems =    null;
        if (cursor != null&& cursor.moveToFirst()) {
            cursor.moveToFirst();
            newitems = cursorToitems(cursor);

        }
        cursor.close();
        return newitems;
    }


    public List<Items> getAllitems() {
        List<Items> listItems = new ArrayList<Items>();

        Cursor cursor = mDatabase.query(TABLE_ITEM, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Items items = cursorToitems(cursor);
                listItems.add(items);
                cursor.moveToNext();
            }

            // make sure to close the cursor
            cursor.close();
        }
        return listItems;
    }


    public List<Items> getAllitemsforviews(int id) {
        List<Items> listItems = new ArrayList<Items>();


        String where = "itm_visit_id=? AND item_remote_status=? ";
        String[] args = {String.valueOf(id), "1"};

        Cursor cursor = mDatabase.query(TABLE_ITEM, mAllColumns, where, args, null, null, null);



//        Cursor cursor = mDatabase.query(DBHelper.TABLE_ITEM, mAllColumns,
//                DBHelper.COLUMN_ITEM_VISIT_ID + " = ?",
//                new String[]{String.valueOf(id)}, null, null, null);


        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Items items = cursorToitems(cursor);
                listItems.add(items);
                cursor.moveToNext();
            }

            // make sure to close the cursor

            cursor.close();
        }
        return listItems;
    }

    public List<Items> getItemsToSyncRemote() {
        List<Items> listItems = new ArrayList<Items>();

        Cursor cursor = mDatabase.query(TABLE_ITEM, mAllColumns,
                DBHelper.COLUMN_ITEM_REMOTE_status + " = ?",
                new String[]{String.valueOf(2)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Items items = cursorToitems(cursor);
                listItems.add(items);
                cursor.moveToNext();
            }

            // make sure to close the cursor

            cursor.close();
        }
        return listItems;
    }

    public Items getitemsbyid(int id) {
        Cursor cursor = mDatabase.query(TABLE_ITEM, mAllColumns,
                DBHelper.COLUMN_ITEM_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Items items = cursorToitems(cursor);
        return items;
    }


    public int Items_get_unsync_count() {
        int item_count = 0;
        Cursor cursor = mDatabase.query(TABLE_ITEM, mAllColumns,
                DBHelper.COLUMN_ITEM_REMOTE_status + " = ?",
                new String[]{String.valueOf(2)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Items items = cursorToitems(cursor);
            item_count =  cursor.getCount();
        }
        return item_count;
    }

    public int Items_not_finished_count(int visit_id) {

        String where = "(itm_visit_id=? AND item_remote_status=?)";
        String[] args = {String.valueOf(visit_id), String.valueOf(1)};

        int item_count = 0;
        Cursor cursor = mDatabase.query(TABLE_ITEM, mAllColumns,
                where,
                args, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Items items = cursorToitems(cursor);
            item_count =  cursor.getCount();
        }
        return item_count;
    }

    protected Items cursorToitems(Cursor cursor) {

        Items item = new Items();
        item.setItm_id(cursor.getInt(0));
        item.setItm_vst_id(cursor.getInt(1));
        item.setItm_name(cursor.getString(2));
        item.setItm_chk_sts(cursor.getInt(3));
        item.setItm_commnt(cursor.getString(4));
        item.setItm_lat(cursor.getString(5));
        item.setItm_lng(cursor.getString(6));
        item.setItm_time(cursor.getString(7));
        item.setItm_rmo_sts(cursor.getInt(8));
        return item;
    }


    public Items update_items(int rowid, int visit_id, String comment, String lati, String longi, String time, int check_staus){

        String where = "(item_id=? AND itm_visit_id=?)";
        String[] args = {String.valueOf(rowid), String.valueOf(visit_id)};

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ITEM_CHECK_STATUS, check_staus);
        values.put(DBHelper.COLUMN_ITEM_COMMENT, comment);
        values.put(DBHelper.COLUMN_ITEM_LAT, lati);
        values.put(DBHelper.COLUMN_ITEM_LNG, longi);
        values.put(DBHelper.COLUMN_ITEM_SUB_TIME, time);
        values.put(DBHelper.COLUMN_ITEM_REMOTE_status, 2);
        int updatedId = mDatabase.update(TABLE_ITEM, values, where, args);
        return null;
    }

    public void deleteItems(int id, int visit_id) {

        String where = "item_id=? AND itm_visit_id=? ";
        String[] args = {String.valueOf(id), String.valueOf(visit_id)};

        int delete = mDatabase.delete(DBHelper.TABLE_ITEM, where, args);
    }

    public void deleteAllItems() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TABLE_ITEM, null, null);
    }





}
