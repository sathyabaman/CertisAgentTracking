package lk.agent.certislanka.certisagenttracking;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import lk.agent.certislanka.certisagenttracking.data.ItemsDAO;
import lk.agent.certislanka.certisagenttracking.data.OfficersDAO;
import lk.agent.certislanka.certisagenttracking.model.Officers;


public class VisitsDetailsActivity extends Activity {


    private ItemsDAO mitemsDAO;
    private OfficersDAO mofficersDAO;
    List<Officers> officerItems;
    String officers_display_text = " ";

    private  String vlat;
    private String vlong;
    private String vplace;
    private int vid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);



        vid             = getIntent().getExtras().getInt("vid");
        int vschID      = getIntent().getExtras().getInt("vschID");
        String vname    = getIntent().getExtras().getString("vname");
        vplace          = getIntent().getExtras().getString("vplace");
        String vtime    = getIntent().getExtras().getString("vtime");
        String vaddress = getIntent().getExtras().getString("vaddress");
        String vtelephone = getIntent().getExtras().getString("vtelephone");
        vlat            = getIntent().getExtras().getString("vlat");
        vlong           = getIntent().getExtras().getString("vlong");
        int vstatus     = getIntent().getExtras().getInt("vstatus");
        final String sdate    = getIntent().getExtras().getString("sdate");


        int str_count = vaddress.split(Pattern.quote("|")).length;
        String[] parts = vaddress.split(Pattern.quote("|"));
        String date = vtime;
        String[] time = date.split(" ");

        TextView tv_customer = (TextView) findViewById(R.id.tv_new_cus);
        TextView tv_date = (TextView) findViewById(R.id.tv_new_date);
        TextView tv_loca = (TextView) findViewById(R.id.tv_new_loc);
        TextView tv_time = (TextView) findViewById(R.id.tv_new_tim);
        TextView tv_cont = (TextView) findViewById(R.id.tv_new_contact);
        TextView tv_addr = (TextView) findViewById(R.id.tv_new_add);
        TextView tv_lbl_location = (TextView) findViewById(R.id.textView56);


        if (!("Individual").equals(vname)) {
            tv_customer.setText(vname);
        }else{
            tv_customer.setText(vplace);
        }


        tv_date.setText(sdate);
        if (!("Individual").equals(vname)) {
            tv_loca.setText(vplace);
        }else{
            tv_loca.setVisibility(View.GONE);
            tv_lbl_location.setVisibility(View.GONE);
        }
        tv_time.setText(time[1] + " " + time[2]);
        tv_cont.setText(vtelephone);

        tv_addr.setText(parts[0]);

        for(int k = 1; k < str_count; k++){
            tv_addr.append("\n"+ parts[k]);
        }
        //Toast.makeText(this, vname, Toast.LENGTH_LONG).show();




        Button map = (Button) findViewById(R.id.btn_map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(VisitsDetailsActivity.this, Visit_Location.class);
                intent.putExtra("latitu", vlat);
                intent.putExtra("longit", vlong);
                intent.putExtra("dest_loc", vplace);
                startActivity(intent);
            }
        });


        String currentdate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Button btn_itm = (Button) findViewById(R.id.btn_task);

            btn_itm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VisitsDetailsActivity.this, Item_Activity.class);
                    intent.putExtra("visit_id", vid);
                    intent.putExtra("visit_date", sdate);
                    startActivity(intent);
                }
            });



        get_officers_list(vid);
        TextView tv_officers = (TextView) findViewById(R.id.tv_titl_schedule);
        tv_officers.setText(officers_display_text);

    }





    private void get_officers_list(int visitid){
        mofficersDAO =new OfficersDAO(getApplicationContext());
        officerItems = mofficersDAO.getAllOfficersforView(visitid);


        for(int i=0; i<officerItems.size(); i++){
            String officer_code = officerItems.get(i).getOff_itm_code();
            String officer_count = officerItems.get(i).getOff_itm_count();

            officers_display_text = officers_display_text + "  "+ officer_code +" : "+officer_count+"  ";
        }

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_visits_details, menu);
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



}
