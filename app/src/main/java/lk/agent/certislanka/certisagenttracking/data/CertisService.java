package lk.agent.certislanka.certisagenttracking.data;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import lk.agent.certislanka.certisagenttracking.R;
import lk.agent.certislanka.certisagenttracking.mainmenu;
import lk.agent.certislanka.certisagenttracking.model.Locations;


public class CertisService extends IntentService {

    GPSTracker gps;
    private double latitude;
    private double longitude;
    private String value;
    private String key;
    private float battery;
    private String gpskey;

    private LocationsDAO mlocationDOA;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */

    public CertisService() {
        super("CertisService");
    }


//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // We want this service to continue running until it is explicitly
//        // stopped, so return sticky.
//        return START_STICKY;
//    }



    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.e("Service Example", "Service Started.. ");
        // pushBackground();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.e("Service Example", "Service Destroyed.. ");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int tt = 1;

        while (tt == 1) {

            get_my_gps_location();
            String lati = String.valueOf(latitude);
            String longi = String.valueOf(longitude);
            battery = getBatteryLevel();
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
            String battery_level = String.valueOf(battery);
            try {
                gpskey = readgpskeyfromfille();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                key = readkeyfromfille();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Log.e("Service Example", " " + i);
            try {

                if(!isNetworkAvailable()){
                    //saving to local database.
                    Insert_location_data(key, lati, longi, battery_level, timeStamp, gpskey);

                }else{
                    //if there is internet connection
                    //check if there is a data in the local database
                        int count = locations_count_tosync();

                        if(count > 0){

                            List<Locations> listlocations =  get_locations_to_sync_remote_db();

                            for (int j=0; j<count; j++){

                                Locations locations = listlocations.get(j);

                                String key = locations.getKey();
                                String lat = locations.getAgent_bg_location_lat();
                                String lng = locations.getAgent_bg_location_lng();
                                String bat = locations.getAgent_bg_battery();
                                String dat1 = locations.getAgent_bg_app_date();
                                String gps1 = locations.getGps_key();

                                try {
                                    // send the data from local to remote
                                    String value = new mylocation().execute(key, lat, lng, bat, dat1, gps1).get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                            //delete location data from local
                            delete_locationdata();
                        }

                    value = new mylocation().execute(key, lati, longi, battery_level, timeStamp, gpskey).get();
                }

                Thread.sleep(120000);
               // Thread.sleep(30000);

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }


    // other classes for services/////////////////////////////////////////////////////////////////////////////////

    private void delete_locationdata(){
        mlocationDOA =new LocationsDAO(getApplicationContext());
        mlocationDOA.deleteAllLocationData();
    }

    private int locations_count_tosync(){
        mlocationDOA =new LocationsDAO(getApplicationContext());
        int count = mlocationDOA.Locations_get_unsync_count();
        return count;
    }

    private List<Locations> get_locations_to_sync_remote_db(){
        mlocationDOA =new LocationsDAO(getApplicationContext());
        List<Locations> listlocations = new ArrayList<Locations>();
        listlocations = mlocationDOA.getLocationsToremoteSync();
        return listlocations;
    }

    private void get_my_gps_location(){

        gps = new GPSTracker(CertisService.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            //gps.showSettingsAlert();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
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

    private String readgpskeyfromfille() throws IOException {
        FileInputStream fis = openFileInput("gpskey.txt");
        BufferedInputStream bis = new BufferedInputStream(fis);
        StringBuffer b = new StringBuffer();

        while(bis.available() !=0){
            char c = (char) bis.read();
            b.append(c);
        }

        String Key =b.toString();
        return Key;
    }




    private boolean Insert_location_data(String key, String lat, String lng, String batt, String dat, String gpskey){
        mlocationDOA =new LocationsDAO(getApplicationContext());
        Locations createdschedule = mlocationDOA.createlocation(key, lat, lng, batt, dat, gpskey);
        return true;
    }


    //  async task to get the schedule, visits, items details from remote database
    class mylocation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://45.40.163.175/web/index.php/api/rest/set-bg-data/");

            try {
                // Add your data
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(5);
                nameValuePairs.add(new BasicNameValuePair("key", arg0[0]));
                nameValuePairs.add(new BasicNameValuePair("agent_bg_location_lat", arg0[1]));
                nameValuePairs.add(new BasicNameValuePair("agent_bg_location_lng", arg0[2]));
                nameValuePairs.add(new BasicNameValuePair("agent_bg_battery", arg0[3]));
                nameValuePairs.add(new BasicNameValuePair("agent_bg_app_date", arg0[4]));
                nameValuePairs.add(new BasicNameValuePair("gps_key", arg0[5]));

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
