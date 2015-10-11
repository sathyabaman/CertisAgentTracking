package lk.agent.certislanka.certisagenttracking;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import lk.agent.certislanka.certisagenttracking.data.ScheduleDAO;
import lk.agent.certislanka.certisagenttracking.model.Schedule;


public class Schedule_Activity extends ListActivity {

    private static final String TAG = "Main Activity";
    private ScheduleDAO mscheduleDAo;
    List<Schedule> ListSchedule;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        list_all_schedules();

        int total = no_of_schedule();
        if (total>0){}else{
            String title = "No Schedules!";
            String body = "Currently no schedules assigned to you!";
            show_message(body, title);
        }

        TextView tvtitle = (TextView) findViewById(R.id.tv_titl_schedule);
        tvtitle.setText("Name \t \t \t \t \t \t Schedule Date");
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Schedule schdul = ListSchedule.get(position);
        int sch_id = schdul.getId();
        String sch_stng_id =Integer.toString(sch_id);;
        String sch_name = schdul.getName();

        Intent intent = new Intent(this, Visits_Activity.class);
        intent.putExtra("sch_id", schdul.getId());
        intent.putExtra("sch_name", schdul.getName());
        intent.putExtra("sch_date", schdul.getDate());

       // Toast.makeText(this, sch_stng_id + " : " + sch_name, Toast.LENGTH_LONG).show();
        startActivity(intent);
    }


    private void list_all_schedules(){

        mscheduleDAo =new ScheduleDAO(getApplicationContext());
        ListSchedule = mscheduleDAo.getAllschedules();
        ArrayAdapter<Schedule> adapter = new ArrayAdapter<Schedule>(this, R.layout.schedule_layout_list, R.id.tv_text1, ListSchedule);

        setListAdapter(adapter);
    }

    private int no_of_schedule(){
        int count = 0;
        mscheduleDAo =new ScheduleDAO(getApplicationContext());
        count = mscheduleDAo.get_schedule_count();
        return count;
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_schedule_, menu);
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



    private void show_message(String data, String title){
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
    