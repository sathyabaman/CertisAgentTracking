package lk.agent.certislanka.certisagenttracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class resetpassword extends Activity {

    private String authKey;
    private String invalid_key = "bm91c2VyfDE5OTAtMDAtMDAgMDA6MDA6MDA=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        //Resetting password

        Button reset = (Button) findViewById(R.id.btn_reset);

        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String data, title, value;
                EditText pass1 = (EditText) findViewById(R.id.et_pass1);
                EditText pass2 = (EditText) findViewById(R.id.et_pass2);

                String password1 = pass1.getText().toString();
                String password2 = pass2.getText().toString();

                if (password1.equals(password2)) {


                    if(isNetworkAvailable()) {
                        value = null;
                        //get the auth key from text file
                        try {
                            readkeyfromfille();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        //send values to async task
                        try {
                            value = new MyTask().execute(authKey, password2).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }


                        //check the return value from server
                        try {
                            check_reset_sucess(value);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //show_message(data = "Password changed successfully!", title = "Success!");
                    }else{show_error_message(data = "Please check your mobile network connection!", title = "Error Occured!");}


                } else {

                    show_error_message(data = "Passwords don't match!", title = "Error Occured!");

                }

            }
        });
    }


    class MyTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... arg0) {


            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://45.40.163.175/web/index.php/api/rest/password-reset/");

            try {
                // Add your data
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("key", arg0[0]));
                nameValuePairs.add(new BasicNameValuePair("agent_password", arg0[1]));
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


    private void check_reset_sucess(String value) throws JSONException {
        JSONObject jObject = new JSONObject(value);

        String code = jObject.getString("code");
        String message = jObject.getString("message");

        switch(code){

            case "1":
                try {
                    writeKeyToFile(invalid_key);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                show_message(message, "Sucessfull!");
                break;

            case "2":
                show_error_message(message, "Error Occured!");
                break;

            case "4":
                reditectto_login(message, "Error Occured!");
                break;
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

    private String readkeyfromfille() throws IOException {
        FileInputStream fis = openFileInput("myappkey.txt");
        BufferedInputStream bis = new BufferedInputStream(fis);
        StringBuffer b = new StringBuffer();

        while(bis.available() !=0){
            char c = (char) bis.read();
            b.append(c);
        }

        return authKey = b.toString();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_resetpassword, menu);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void show_message(String data, String title){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        startActivity(new Intent(resetpassword.this, MainActivity.class));
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void reditectto_login(String data, String title){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        startActivity(new Intent(resetpassword.this, MainActivity.class));
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void show_error_message(String data, String title){
        new AlertDialog.Builder(this)
                .setTitle(title)
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
