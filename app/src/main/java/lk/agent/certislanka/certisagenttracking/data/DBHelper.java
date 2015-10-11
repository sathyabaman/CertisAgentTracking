package lk.agent.certislanka.certisagenttracking.data;

/**
 * Created by administrator on 7/7/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by administrator on 7/6/15.
 */
public class DBHelper extends SQLiteOpenHelper {


    public static final String TAG = "DBHelper";

    // columns of the schedule table
    public static final String TABLE_SCHEDULE= "schedule";
    public static final String COLUMN_SCHEDULE_ID = "schedule_id";
    public static final String COLUMN_SCHEDULE_NAME = "schedule_name";
    public static final String COLUMN_SCHEDULE_DATE = "schedule_date";

    // columns of the items table
    public static final String TABLE_ITEM= "items";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_ITEM_VISIT_ID = "itm_visit_id";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_ITEM_CHECK_STATUS= "item_check_status";
    public static final String COLUMN_ITEM_COMMENT = "item_comment";
    public static final String COLUMN_ITEM_LAT = "item_latitude";
    public static final String COLUMN_ITEM_LNG = "item_longitude";
    public static final String COLUMN_ITEM_SUB_TIME = "item_submit_time";
    public static final String COLUMN_ITEM_REMOTE_status = "item_remote_status";

    // columns of the visits table
    public static final String TABLE_VISITS = "visits";
    public static final String COLUMN_VISITS_ID = "visit_id";
    public static final String COLUMN_VISITS_SCHEDULE_ID = "schedule_id";
    public static final String COLUMN_VISITS_NAME = "visit_name";
    public static final String COLUMN_VISITS_TIME = "visit_time";
    public static final String COLUMN_VISITS_PLACE = "visit_place";
    public static final String COLUMN_VISITS_ADDRESS ="visit_address";
    public static final String COLUMN_VISITS_TEL = "visit_telephone";
    public static final String COLUMN_VISITS_LOCATION_LAT = "visit_location_lat";
    public static final String COLUMN_VISITS_LOCATION_LNG = "visit_location_lng";
    public static final String COLUMN_VISITS_STATUS = "visit_status";


    // columns of the locations data table
    public static final String TABLE_LOCATION_DATA = "locationdata";
    public static final String COLUMN_LOC_ID = "id";
    public static final String COLUMN_LOC_KEY = "key";
    public static final String COLUMN_LOC_LAT = "agent_bg_location_lat";
    public static final String COLUMN_LOC_LNG = "agent_bg_location_lng";
    public static final String COLUMN_LOC_BATTERY= "agent_bg_battery";
    public static final String COLUMN_LOC_DATE = "agent_bg_app_date";
    public static final String COLUMN_LOC_GPSKEY = "gps_key";


    // columns of the  field officers table
    public static final String TABLE_FIELD_OFFICERS = "fieldofficers";
    public static final String COLUMN_FIELD_ID = "off_id";
    public static final String COLUMN_VISIT_ID = "off_visit_id";
    public static final String COLUMN_ITEM_CODE = "off_item_code";
    public static final String COLUMN_ITEM_COUNT = "offitem_count";

    private static final String DATABASE_NAME = "certisagentTrackingdb";
    private static final int DATABASE_VERSION = 13;


    // SQL statement of the visits table creation
    private static final String SQL_CREATE_TABLE_VISITS = "CREATE TABLE " + TABLE_VISITS + "("
            + COLUMN_VISITS_ID 				+ " INTEGER PRIMARY KEY, "
            + COLUMN_VISITS_SCHEDULE_ID 	+ " INTEGER, "
            + COLUMN_VISITS_NAME 			+ " TEXT NOT NULL, "
            + COLUMN_VISITS_TIME 			+ " TEXT NOT NULL, "
            + COLUMN_VISITS_PLACE 			+ " TEXT NOT NULL, "
            + COLUMN_VISITS_ADDRESS 		+ " TEXT NOT NULL, "
            + COLUMN_VISITS_TEL 			+ " TEXT NOT NULL, "
            + COLUMN_VISITS_LOCATION_LAT 	+ " TEXT NOT NULL, "
            + COLUMN_VISITS_LOCATION_LNG 	+ " TEXT NOT NULL, "
            + COLUMN_VISITS_STATUS 			+ " INTEGER "
            +");";


    // SQL statement of the schedule table creation
    private static final String SQL_CREATE_TABLE_SCHEDULE = "CREATE TABLE " + TABLE_SCHEDULE + "("
            + COLUMN_SCHEDULE_ID 		+ " INTEGER PRIMARY KEY, "
            + COLUMN_SCHEDULE_NAME 		+ " TEXT NOT NULL, "
            + COLUMN_SCHEDULE_DATE 		+ " TEXT NOT NULL "
            +");";

    // SQL statement of the item table creation
    private static final String SQL_CREATE_TABLE_LOCATION = "CREATE TABLE " + TABLE_LOCATION_DATA + "("
            + COLUMN_LOC_ID 			+ " INTEGER AUTO INCREMENT PRIMARY KEY, "
            + COLUMN_LOC_KEY 		    + " TEXT, "
            + COLUMN_LOC_LAT 			+ " TEXT, "
            + COLUMN_LOC_LNG 	        + " TEXT, "
            + COLUMN_LOC_BATTERY 		+ " TEXT, "
            + COLUMN_LOC_DATE 		    + " TEXT, "
            + COLUMN_LOC_GPSKEY 		+ " TEXT "
            +");";


    // SQL statement of the item table creation
    private static final String SQL_CREATE_TABLE_ITEMS = "CREATE TABLE " + TABLE_ITEM + "("
            + COLUMN_ITEM_ID 			+ " INTEGER, "
            + COLUMN_ITEM_VISIT_ID 		+ " INTEGER, "
            + COLUMN_ITEM_NAME 			+ " TEXT NOT NULL, "
            + COLUMN_ITEM_CHECK_STATUS 	+ " INTEGER, "
            + COLUMN_ITEM_COMMENT 		+ " TEXT, "
            + COLUMN_ITEM_LAT 		    + " TEXT, "
            + COLUMN_ITEM_LNG 		    + " TEXT, "
            + COLUMN_ITEM_SUB_TIME 		+ " TEXT, "
            + COLUMN_ITEM_REMOTE_status + " INTEGER "
            +");";


    // SQL statement of the field officers table creation
    private static final String SQL_CREATE_TABLE_FIELD_OFFICERS = "CREATE TABLE " + TABLE_FIELD_OFFICERS + "("
            + COLUMN_FIELD_ID 			+ " INTEGER AUTO INCREMENT PRIMARY KEY, "
            + COLUMN_VISIT_ID 		    + " TEXT, "
            + COLUMN_ITEM_CODE 			+ " TEXT, "
            + COLUMN_ITEM_COUNT 	    + " TEXT "
            +");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE_SCHEDULE);
        database.execSQL(SQL_CREATE_TABLE_VISITS);
        database.execSQL(SQL_CREATE_TABLE_ITEMS);
        database.execSQL(SQL_CREATE_TABLE_LOCATION);
        database.execSQL(SQL_CREATE_TABLE_FIELD_OFFICERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG,
                "Upgrading the database from version " + oldVersion + " to " + newVersion);
        // clear all data
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIELD_OFFICERS);

        // recreate the tables
        onCreate(db);
    }

}
