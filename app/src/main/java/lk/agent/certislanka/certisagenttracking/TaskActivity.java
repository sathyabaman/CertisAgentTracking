package lk.agent.certislanka.certisagenttracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import lk.agent.certislanka.certisagenttracking.data.GPSTracker;
import lk.agent.certislanka.certisagenttracking.data.ItemsDAO;
import lk.agent.certislanka.certisagenttracking.model.Items;

public class TaskActivity extends Activity {

    private ItemsDAO mitemsDAO;
    private int check_status;
    private boolean update;
    private String comment;
    private int task_id;
    private double latitude = 0;
    private double longitude =0;
    private String timeStamp;
    private String value = null;
    private int visit_id;
    private String visit_strong_date;
    GPSTracker gps;


    private String invalid_key = "bm91c2VyfDE5OTAtMDAtMDAgMDA6MDA6MDA=";
    private String currentdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        String task_name = getIntent().getExtras().getString("itm_name");
        task_id = getIntent().getExtras().getInt("item_id");
        visit_id = getIntent().getExtras().getInt("visit_id");
        visit_strong_date = getIntent().getExtras().getString("visit_date");
        currentdate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        TextView tv_name = (TextView) findViewById(R.id.tv_task_name);
        tv_name.setText("Task Name : " + task_name);


        Button btn_submit  = (Button) findViewById(R.id.btn_task_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                            get_selected_item();
                            send_data_to_server();


            }
        });
    }


    private void send_data_to_server(){

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (statusOfGPS) {

            try {
                String task_id_in_sting = String.valueOf(task_id);
                String key = readkeyfromfille();
                timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                show_my_gps_location();

                final String user_key = key;
                final String task_id = task_id_in_sting;
                final int CheckStatus = check_status;
                final String task_comment = comment;
                final String submit_time = timeStamp;
                final double task_latitude = latitude;
                final double task_longitude = longitude;


                if (check_status == 0) {
                    String message1 = "Please Select a task status.";
                    show_message(message1);


                } else if (check_status == 1) {

//                    if (!isNetworkAvailable()) {
                        update = update_items();
                        if (update) {
                            String data2 = "Task updated successfully. Thank you.";
                            show_message_sucess(data2);
                        }
//                    } else {
//
////                        value = new Submit_task().execute(user_key, task_id, String.valueOf(CheckStatus), task_comment, submit_time, String.valueOf(task_latitude), String.valueOf(task_longitude), String.valueOf(visit_id)).get();
////                        doNecessaryAction();
//
//                        if (update) {
//                            String data2 = "Task updated successfully. Thank you.";
//                            show_message_sucess(data2);
//                        }
//
//                    }


                } else {

                    // check if there is a comment for partially completed and in completed.
                    if (comment != null && !comment.isEmpty()) {

//                        if (!isNetworkAvailable()) {
                            update = update_items();
                            if (update) {
                                String data2 = "Task updated successfully. Thank you.";
                                show_message_sucess(data2);
                            }
//                        } else {
//
////                            value = new Submit_task().execute(user_key, task_id, String.valueOf(CheckStatus), task_comment, submit_time, String.valueOf(task_latitude), String.valueOf(task_longitude), String.valueOf(visit_id)).get();
////                            doNecessaryAction();
//                            if (update) {
//                                String data2 = "Task updated successfully. Thank you.";
//                                show_message_sucess(data2);
//                            }
//
//                        }

                    } else {
                        String comment_error_message = "Please add a comment.";
                        show_message(comment_error_message);
                    }
                }

                //count_items_tosync();
                //show_message(sendstask);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            show_my_gps_location();
        }

    }
    private void writeKeyToFile(String key) throws IOException {

        File f = getFilesDir();
        String path = f.getAbsolutePath();
        // show_message(path);

        FileOutputStream fos =openFileOutput("myappkey.txt", MODE_PRIVATE);
        fos.write(key.getBytes());
        fos.close();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void count_items_tosync(){
        mitemsDAO =new ItemsDAO(getApplicationContext());
        int count = mitemsDAO.Items_get_unsync_count();
        show_message_sucess(String.valueOf(count));
    }

    private void doNecessaryAction() throws JSONException {
        JSONObject jObject = new JSONObject(value);
        String code = jObject.getString("code");

        switch (code) {

            case "1":

                String data ="Task Completed Sucessfully. Thank you.";
                //delete from local database
                mitemsDAO =new ItemsDAO(getApplicationContext());
                mitemsDAO.deleteItems(task_id, visit_id);

                show_message_sucess(data);
                //must write code to delete task from database
                break;

            case "2":
                update = update_items();
                if(update){
                    String data2 ="Task Completed Sucessfully. Thank you.";
                    show_message_sucess(data2);
                }
                break;

            case "4":
                    String data2 ="your session is expired. Please Login again";
                show_home_message(data2);
                break;

            default:
                break;

        }
    }


    private boolean update_items(){
        mitemsDAO =new ItemsDAO(getApplicationContext());
        String lati = String.valueOf(latitude);
        String longi = String.valueOf(longitude);
        Items upate_itm = mitemsDAO.update_items(task_id, visit_id, comment, lati, longi, timeStamp, check_status);
        return true;
    }

    private void show_my_gps_location(){

        gps = new GPSTracker(TaskActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        }else{


            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
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


    public void get_selected_item(){
        Spinner spn_task_status = (Spinner) findViewById(R.id.spn_task_status);
        String task_staus = spn_task_status.getSelectedItem().toString();
        switch(task_staus) {

            case "None":
                check_status = 0;
                break;
            case "Complete":
                check_status = 1;
                break;
            case "Partially Complete":
                check_status = 2;
                break;
            case "incomplete":
                check_status = 3;
                break;
            default:
                check_status = 3;
                break;
        }

        EditText et_comment = (EditText) findViewById(R.id.et_comment);
        comment = et_comment.getText().toString();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                    }
//                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }



    class Submit_task extends AsyncTask<String, Void, String> {

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


    private void show_message_sucess(String data){
        new AlertDialog.Builder(this)
                .setTitle("Successful!")
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int which) {

                        //go back to task list

                        //finish();

                        Intent intent = new Intent(TaskActivity.this, Item_Activity.class);
                        intent.putExtra("visit_id", visit_id);
                        intent.putExtra("visit_date", visit_strong_date);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void show_home_message(String data){
        new AlertDialog.Builder(this)
                .setTitle("Error!")
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int which) {

                        //go back to task list

                        //finish();
                        try {
                            writeKeyToFile(invalid_key);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(TaskActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }



}
