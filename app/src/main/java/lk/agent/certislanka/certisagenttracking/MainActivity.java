package lk.agent.certislanka.certisagenttracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lk.agent.certislanka.certisagenttracking.data.ItemsDAO;
import lk.agent.certislanka.certisagenttracking.data.OfficersDAO;
import lk.agent.certislanka.certisagenttracking.data.ScheduleDAO;
import lk.agent.certislanka.certisagenttracking.data.VisitsDAO;
import lk.agent.certislanka.certisagenttracking.model.Items;
import lk.agent.certislanka.certisagenttracking.model.Officers;
import lk.agent.certislanka.certisagenttracking.model.Schedule;
import lk.agent.certislanka.certisagenttracking.model.Visits;


public class MainActivity extends Activity {

    private ScheduleDAO mscheduleDAo;
    private VisitsDAO mvisitsDAO;
    private ItemsDAO mitemsDAO;
    private OfficersDAO mofficersDAO;
    private String storedkey;
    private String jsonfromapi;
    private String temp_auth_key;
    public String android_app_version ="1.3.15.08.27";

    //schedule table variables
    private  String schedule_id = null;
    private  String schedule_name = null;
    private  String schedule_date= null;

    //visits table variables
    private String visit_id 			= null;
    private String visit_schedule_id 	= null;
    private String visit_name 			= null;
    private String visit_time 			= null;
    private String visit_place 			= null;
    private String visit_address 		= null;
    private String visit_telephone      = null;
    private String visit_location_lat 	= null;
    private String visit_location_lng 	= null;
    private String visit_status 		= null;

    //item table variables
    private String item_id                  = null;
    private String itm_visit_id             = null;
    private String item_name                = null;
    private String item_check_status        = null;
    private String item_comment             = null;
    private String item_latitude            = null;
    private String item_longitude           = null;
    private String item_submit_time         = null;
    private String item_remote_status       = null;

    //fieldofficers table
    private String off_id                   = null;
    private String off_visit_id             = null;
    private String off_item_code            = null;
    private String offitem_count            = null;


    private String value = null;


    String auth_code;
    String GPS_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        //checking the local date with server date
        try {
            String local_date = getdate_from_local();
            String today_date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

            if(local_date.equals(today_date)){
                startActivity(new Intent(MainActivity.this, mainmenu.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);
        if(!isNetworkAvailable()){
            show_message("Please check your mobile network, and try to re-login !");
        }
        //String device2 = "deviceMan |model |deviceName |imei";
        final String device2 = get_device_details();


        byte[] val = new byte[0];
        try {
            val = device2.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Read fail from local.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        final String device = Base64.encodeToString(val, Base64.DEFAULT);

        //loading forgot password activity
        Button fgot_pass = (Button) findViewById(R.id.btn_forgot_password);
        fgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            }
        });

        //loading login activity
        Button login = (Button) findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!isNetworkAvailable()) {
                    show_message("Please check your mobile network, and try to re -login !");
                } else {

                    //first uploading all the finished items in the local databse
                    int total_itm = count_items_tosync();
                    // show_message(String.valueOf(total_itm));

                    if(total_itm > 0){
                        try {
                            temp_auth_key = readkeyfromfille();
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Read fail from local.", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                        //get all data from local
                        List<Items> listItems =   get_items_to_sync_remote_db();

                        for (int i=0; i<total_itm; i++){

                            Toast.makeText(getApplicationContext(), "Syncing data "+i+" to Certis Server. Please Wait! ", Toast.LENGTH_LONG).show();
                            Items items = listItems.get(i);

                            int item_id =items.getItm_id();
                            int CheckStatus = items.getItm_chk_sts();
                            String comment = items.getItm_commnt();
                            String latitude = items.getItm_lat();
                            String longitude = items.getItm_lng();
                            String time = items.getItm_time();
                            int visit_id = items.getItm_vst_id();
                            try {
                                value = new Submit_unsynced_task().execute(temp_auth_key, String.valueOf(item_id), String.valueOf(CheckStatus),
                                        comment, time, latitude, longitude, String.valueOf(visit_id)).get();
                            } catch (InterruptedException e) {
                                Toast.makeText(getApplicationContext(), "No Response from Remote Server", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                Toast.makeText(getApplicationContext(), "No Response from Remote Server", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }

                    //end of uploading local db

                    EditText username = (EditText) findViewById(R.id.et_user_name);
                    EditText password = (EditText) findViewById(R.id.et_password);

                    String user_name = username.getText().toString();
                    String paswrd = password.getText().toString();
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

                    String data2 = user_name + "|" + paswrd + "|" + timeStamp +"|" + android_app_version;

                    byte[] data = new byte[0];
                    try {
                        data = data2.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String base64 = Base64.encodeToString(data, Base64.DEFAULT);


                    try {
                        value = new MyTask().execute(base64, device).get();
                    } catch (InterruptedException e) {
                        Toast.makeText(getApplicationContext(), "No Response from Remote Server", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        Toast.makeText(getApplicationContext(), "No Response from Remote Server", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    try {
                        checklogin(value);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "No Response from Remote Server", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "No Response from Remote Server", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }


                }
            }
        });
    }


    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Logging In...", true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    checklogin(value);
                    // Here you should write your time consuming task...
                    // Let the progress ring for 10 seconds...
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }

    private int count_items_tosync(){
        mitemsDAO =new ItemsDAO(getApplicationContext());
        int count = mitemsDAO.Items_get_unsync_count();
        return count;
    }

    private List<Items> get_items_to_sync_remote_db(){
        mitemsDAO =new ItemsDAO(getApplicationContext());
        List<Items> listItems = new ArrayList<Items>();
        listItems = mitemsDAO.getItemsToSyncRemote();
        return listItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getdate_from_local() throws IOException {

        String storedkey = readkeyfromfille();
        byte[] data = Base64.decode(storedkey, Base64.DEFAULT);
        String key_in_text = new String(data, "UTF-8");
        String[] parts = key_in_text.split(" ");
        String string1 = parts[0]; // 004
        String string2 = parts[1];
        String date = string1.substring(string1.length() - 10);

        return date;
    }

    private void writeKeyToFile(String key) throws IOException {

        File f = getFilesDir();
        String path = f.getAbsolutePath();
        // show_message(path);
        FileOutputStream fos =openFileOutput("myappkey.txt", MODE_PRIVATE);
        fos.write(key.getBytes());
        fos.close();
    }

    private String readkeyfromfille() throws IOException {
        FileInputStream fis = openFileInput("myappkey.txt");
        BufferedInputStream bis = new BufferedInputStream(fis);
        StringBuffer b = new StringBuffer();

        while(bis.available() !=0){
            char c = (char) bis.read();
            b.append(c);
        }

        String Key =b.toString();
        return Key;

    }

    private void writeGPSKeyToFile(String key) throws IOException {

        File f = getFilesDir();
        String path = f.getAbsolutePath();
        // show_message(path);

        FileOutputStream fos =openFileOutput("gpskey.txt", MODE_PRIVATE);
        fos.write(key.getBytes());
        fos.close();
    }

    private void checklogin(String value) throws JSONException, IOException {
        JSONObject jObject = new JSONObject(value);

        String code = jObject.getString("code");
        String message = jObject.getString("message");
        auth_code = jObject.getString("auth_key");
        storedkey = jObject.getString("auth_key");
        GPS_code = jObject.getString("gps_key");

        switch(code){

            case "1":

                final ProgressDialog ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Logging In...", true);
                ringProgressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {



                                writeKeyToFile(auth_code);
                                writeGPSKeyToFile(GPS_code);

                                //Delete all records from visits and schedule tables
                                mscheduleDAo = new ScheduleDAO(getApplicationContext());
                                mscheduleDAo.deleteAllSchedule();

                                mvisitsDAO = new VisitsDAO(getApplicationContext());
                                mvisitsDAO.deleteAllVisits();

                                mitemsDAO = new ItemsDAO(getApplicationContext());
                                mitemsDAO.deleteAllItems();

                                mofficersDAO = new OfficersDAO(getApplicationContext());
                                mofficersDAO.deleteAllofficers();

                                // getting the list of schedules from the remote api
                                get_schedule_list();


                                try{
                                    JSONObject jsonObject = new JSONObject(jsonfromapi); //Here reponse is the yours server response
                                    JSONObject result = jsonObject.getJSONObject("result");
                                    JSONArray sehedule = result.getJSONArray("sehedule");

                                    //save all schedules
                                    for(int i=0;i<sehedule.length();i++){

                                        schedule_id = sehedule.getJSONObject(i).getString("schedule_id");
                                        schedule_date = sehedule.getJSONObject(i).getString("schedule_date");
                                        String[] parts = schedule_date.split(" ");
                                        String string1 = parts[0];
                                        schedule_date = string1;
                                        schedule_name = sehedule.getJSONObject(i).getString("schedule_name");

                                        insert_schedule();
                                    }

                                    //save all visits
                                    JSONArray visits = result.getJSONArray("visit");
                                    for(int i=0;i<visits.length();i++){
                                        visit_id = visits.getJSONObject(i).getString("visit_id");
                                        visit_schedule_id = visits.getJSONObject(i).getString("schedule_id");
                                        visit_name = visits.getJSONObject(i).getString("visit_name");
                                        visit_time = visits.getJSONObject(i).getString("visit_time");
                                        visit_place = visits.getJSONObject(i).getString("visit_place");
                                        visit_address = visits.getJSONObject(i).getString("visit_address");
                                        visit_telephone = visits.getJSONObject(i).getString("visit_telephone");
                                        visit_location_lat = visits.getJSONObject(i).getString("visit_location_lat");
                                        visit_location_lng = visits.getJSONObject(i).getString("visit_location_lng");
                                        visit_status = visits.getJSONObject(i).getString("visit_status");
                                        insert_visits();
                                    }

                                    //Save all items
                                    JSONArray items = result.getJSONArray("item");
                                    for(int i=0;i<items.length();i++){
                                        item_id = items.getJSONObject(i).getString("item_id");
                                        itm_visit_id = items.getJSONObject(i).getString("visit_id");
                                        item_name = items.getJSONObject(i).getString("item_name");
                                        item_check_status ="1";
                                        item_comment = "";
                                        item_latitude = "";
                                        item_longitude = "";
                                        item_submit_time = "";
                                        item_remote_status = "1";

                                        insert_items();
                                    }

                                    //Save all field officers
                                    JSONArray field_officers = result.getJSONArray("field_officers");
                                    for(int i=0;i<field_officers.length();i++){
                                        off_visit_id = field_officers.getJSONObject(i).getString("visit_id");
                                        off_item_code = field_officers.getJSONObject(i).getString("field_officer_item_code");
                                        offitem_count = field_officers.getJSONObject(i).getString("field_officer_item_count");
                                        //  Log.d("officers", off_item_code);
                                        insert_field_officers();
                                    }

                                }catch(Exception e) { }

                            Thread.sleep(5000);
                            startActivity(new Intent(MainActivity.this, mainmenu.class));
                        } catch (Exception e) {

                        }
                        ringProgressDialog.dismiss();
                    }
                }).start();


                break;

            case "2":
                show_message(message);
                break;

            case "3":
                writeKeyToFile(auth_code);
                startActivity(new Intent(MainActivity.this, resetpassword.class));
                break;
        }
    }


    private boolean insert_schedule(){
        mscheduleDAo =new ScheduleDAO(getApplicationContext());
        Schedule createdschedule = mscheduleDAo.createschedule(Integer.parseInt(String.valueOf(schedule_id)),
                schedule_name,
                schedule_date);
        return true;
    }

    private boolean insert_visits(){
        mvisitsDAO = new VisitsDAO(getApplicationContext());
        Visits createvisits =mvisitsDAO.createvisits(Integer.parseInt(String.valueOf(visit_id)),
                Integer.parseInt(String.valueOf(visit_schedule_id)),
                visit_name, visit_time, visit_place, visit_address, visit_telephone, visit_location_lat, visit_location_lng,
                Integer.parseInt(String.valueOf(visit_status)));
        return true;
    }

    private boolean insert_items(){
        mitemsDAO = new ItemsDAO(getApplicationContext());
        Items createitems =mitemsDAO.createitems(Integer.parseInt(String.valueOf(item_id)),
                Integer.parseInt(String.valueOf(itm_visit_id)),
                item_name,
                Integer.parseInt(String.valueOf(item_check_status)),
                item_comment, item_latitude, item_longitude, item_submit_time,
                Integer.parseInt(String.valueOf(item_remote_status)));
        return true;
    }

    private boolean insert_field_officers(){
        mofficersDAO =new OfficersDAO(getApplicationContext());
        Officers createdofficers = mofficersDAO.createofficers(off_visit_id,
                off_item_code,
                offitem_count);
        return true;
    }

    private void get_schedule_list(){
        try {
            jsonfromapi  = new MySchedules().execute(storedkey).get();
            //show_message(jsonfromapi);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }




    //async task to get the schedule, visits, items details from remote database
    class MySchedules extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://45.40.163.175/web/index.php/api/rest/get-schedules/");

            try {
                // Add your data
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair("key", arg0[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String schedule_list = EntityUtils.toString(entity, "UTF-8");
                return schedule_list;

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return null;
        }


        @Override
        protected void onPostExecute(String data) {
            if(data != null){
                jsonfromapi = data;  //you would get json data here
                //then do parse your json data
            }
        }
    }
    // end of async task


    class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://45.40.163.175/web/index.php/api/rest/auth");

            try {
                // Add your data
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("key", arg0[0]));
                nameValuePairs.add(new BasicNameValuePair("device", arg0[1]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");

                return responseString;

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return null;
        }
    }

    private void show_message(String data){
        new AlertDialog.Builder(this)
                .setTitle("Error Occured!")
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String get_device_details(){
        String model = android.os.Build.MODEL;
        String deviceMan = android.os.Build.MANUFACTURER;
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();

        String device_details = deviceMan +"|"+model + "|"+deviceName + "|"+imei;
        return device_details;
    }


    class Submit_unsynced_task extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://45.40.163.175/web/index.php/api/rest/set-visit/");

            try {
                // Add your data
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(7);
                nameValuePairs.add(new BasicNameValuePair("key", arg0[0]));
                nameValuePairs.add(new BasicNameValuePair("task_id", arg0[1]));
                nameValuePairs.add(new BasicNameValuePair("check_status", arg0[2]));
                nameValuePairs.add(new BasicNameValuePair("task_comment", arg0[3]));
                nameValuePairs.add(new BasicNameValuePair("submit_time", arg0[4]));
                nameValuePairs.add(new BasicNameValuePair("latitude", arg0[5]));
                nameValuePairs.add(new BasicNameValuePair("longitude", arg0[6]));
                nameValuePairs.add(new BasicNameValuePair("visit_id", arg0[7]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");

                return responseString;

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return null;
        }
    }

}
