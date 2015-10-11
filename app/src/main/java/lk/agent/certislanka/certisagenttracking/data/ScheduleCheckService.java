package lk.agent.certislanka.certisagenttracking.data;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import lk.agent.certislanka.certisagenttracking.R;
import lk.agent.certislanka.certisagenttracking.mainmenu;
import lk.agent.certislanka.certisagenttracking.model.Items;
import lk.agent.certislanka.certisagenttracking.model.Officers;
import lk.agent.certislanka.certisagenttracking.model.Schedule;
import lk.agent.certislanka.certisagenttracking.model.Visits;

import java.util.Iterator;
/**
 * Created by administrator on 7/22/15.
 */
public class ScheduleCheckService extends IntentService {

    private String value;
    private ScheduleDAO mscheduleDAo;
    private VisitsDAO mvisitsDAO;
    private ItemsDAO mitemsDAO;
    private String storedkey;
    private String jsonfromapi;
    private OfficersDAO mofficersDAO;



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

    private String temp_auth_key;




    public ScheduleCheckService() {
        super("ScheduleCheckService");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }


//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // We want this service to continue running until it is explicitly
//        // stopped, so return sticky.
//        return START_STICKY;
//    }



    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.e("Service Schedule", "Service Started.. ");
        // pushBackground();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.e("Service Schedule", "Service Destroyed.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int tk = 1;

        while (tk == 1) {


            //sync all unsynced task to Remote Databases
            if (isNetworkAvailable()) {
            int total_itm = count_items_tosync();
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
            }else {

            }




            //get all new schedules and insert to local database
            try {
                storedkey = readkeyfromfille();
                if (isNetworkAvailable()) {
                    String value = new checkschedules().execute(storedkey).get();
                    DoUpdateSchedule(value);
                } else {}

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private int count_items_tosync(){
        mitemsDAO =new ItemsDAO(getApplicationContext());
        int count = mitemsDAO.Items_get_unsync_count();
        return count;
    }

    private void DoUpdateSchedule(String value) throws JSONException {
            JSONObject jObject = new JSONObject(value);
        String code = jObject.getString("code");
        String result_message = jObject.getString("result");


        switch(code) {

            case "1":

                String popmessage ="";
                JSONArray msg = (JSONArray) jObject.get("result");
                for (int i=0;i<msg.length();i++) {
                    popmessage = popmessage + "\n\n * "+(String) msg.get(i);
                }


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

                    show_lockscreen_notification(popmessage);

                    Intent dialogIntent = new Intent(getBaseContext(), mainmenu.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogIntent.putExtra("message", 1);
                    dialogIntent.putExtra("result_message", popmessage);
                    getApplication().startActivity(dialogIntent);

                }catch(Exception e){ }
                break;

            default:
                //delete this notification its not needed.

                break;
        }
    }

    private void get_schedule_list(){
        try {
            jsonfromapi  = new MySchedules().execute(storedkey).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private List<Items> get_items_to_sync_remote_db(){
        mitemsDAO =new ItemsDAO(getApplicationContext());
        List<Items> listItems = new ArrayList<Items>();
        listItems = mitemsDAO.getItemsToSyncRemote();
        return listItems;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean insert_field_officers(){
        mofficersDAO =new OfficersDAO(getApplicationContext());
        Officers createdofficers = mofficersDAO.createofficers(off_visit_id,
                off_item_code,
                offitem_count);
        return true;
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

    private String readkeyfromfille() throws IOException {
        FileInputStream fis = openFileInput("myappkey.txt");
        BufferedInputStream bis = new BufferedInputStream(fis);
        StringBuffer b = new StringBuffer();

        while(bis.available() !=0){
            char c = (char) bis.read();
            b.append(c);
        }

        String Key = b.toString();
        return Key;
    }

    private void show_lockscreen_notification(String Message){

        Intent resultIntent = new Intent(this, mainmenu.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, resultIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())

                .setContentIntent(pi)
                .setAutoCancel(true)
                .setContentTitle("Your Schedules Updated!")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText(Message)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE| Notification.DEFAULT_LIGHTS);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0, builder.build());
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




    //  async task to get the schedule, visits, items details from remote database
    class checkschedules extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://45.40.163.175/web/index.php/api/rest/get-schedule-update/");

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
                value = data;  //you would get json data here
                //then do parse your json data
            }
        }
    }
    // end of async task


}
