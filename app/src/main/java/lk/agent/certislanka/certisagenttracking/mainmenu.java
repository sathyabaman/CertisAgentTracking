package lk.agent.certislanka.certisagenttracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import lk.agent.certislanka.certisagenttracking.data.CertisService;
import lk.agent.certislanka.certisagenttracking.data.DBHelper;
import lk.agent.certislanka.certisagenttracking.data.ItemsDAO;
import lk.agent.certislanka.certisagenttracking.data.ScheduleCheckService;
import lk.agent.certislanka.certisagenttracking.data.ScheduleDAO;
import lk.agent.certislanka.certisagenttracking.data.VisitsDAO;
import lk.agent.certislanka.certisagenttracking.model.Items;
import lk.agent.certislanka.certisagenttracking.model.Schedule;
import lk.agent.certislanka.certisagenttracking.model.Visits;

public class mainmenu extends Activity {


    private ScheduleDAO mscheduleDAo;
    private VisitsDAO mvisitsDAO;
    private String storedkey;
    private String jsonfromapi;

    private SQLiteDatabase mDatabase;
    private ItemsDAO mitemsDAO;
    List<Items> ListItems;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        Intent intent = getIntent();

        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("message")) {

                int schedule_update_message = getIntent().getExtras().getInt("message");
                String Message_result  = getIntent().getExtras().getString("result_message");
                    if (schedule_update_message == 1) {

                        show_message_schedule_update(Message_result);
                    }
            }
        }



        //loading schedule list

        ImageButton btn_im_sche = (ImageButton) findViewById(R.id.bm_schedule);
        btn_im_sche.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(mainmenu.this, Schedule_Activity.class));
            }
        });


        //loading reset password
        ImageButton reset_pass = (ImageButton) findViewById(R.id.bm_reset_password);
        reset_pass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(mainmenu.this, resetpassword.class));
            }
        });

        //loading google maps

        ImageButton my_location = (ImageButton) findViewById(R.id.bm_viewmap);
        my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mainmenu.this, My_Location.class));
            }
        });



//        try {
//            String user_name = getdate_from_local();
//            TextView user = (TextView) findViewById(R.id.lbl_name);
//            user.setText("Welcome : "+user_name+"\n");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //services
        Intent svc = new Intent(this, CertisService.class);
        startService(svc);

        Intent scheser = new Intent(this, ScheduleCheckService.class);
        startService(scheser);


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

    public String getdate_from_local() throws IOException {

        String storedkey = readkeyfromfille();

        byte[] data = Base64.decode(storedkey, Base64.DEFAULT);
        String key_in_text = new String(data, "UTF-8");


        String[] parts = key_in_text.split(Pattern.quote("|"));
        String string1 = parts[0]; // 004
        String string2 = parts[1];

        return string1;
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


    private void show_message_schedule_update(String data){
        new AlertDialog.Builder(this)
                .setTitle("schedule updated!")
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
