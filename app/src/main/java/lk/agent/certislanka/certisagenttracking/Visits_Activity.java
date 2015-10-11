package lk.agent.certislanka.certisagenttracking;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import lk.agent.certislanka.certisagenttracking.data.DBHelper;
import lk.agent.certislanka.certisagenttracking.data.ItemsDAO;
import lk.agent.certislanka.certisagenttracking.data.VisitsDAO;
import lk.agent.certislanka.certisagenttracking.model.Schedule;
import lk.agent.certislanka.certisagenttracking.model.Visits;

public class Visits_Activity extends ListActivity {

    private SQLiteDatabase mDatabase;
    private VisitsDAO mvisitsDAO;
    private ItemsDAO mitemsDao;
    List<Visits> ListVisits;

    private String schedule_date =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        int schedule_id         = getIntent().getExtras().getInt("sch_id");
        String schedule_name    = getIntent().getExtras().getString("sch_name");
        schedule_date    = getIntent().getExtras().getString("sch_date");

        TextView tv_sch_name  = (TextView) findViewById(R.id.tv_schedule_name);
        tv_sch_name.setText(schedule_name);


       // Toast.makeText(this, " schedule_id "+schedule_id, Toast.LENGTH_LONG).show();
        list_all_visits(schedule_id);


    }



    public void deleteVisits(Visits visits) {
        long id = visits.getvId();
        mDatabase.delete(DBHelper.TABLE_VISITS, DBHelper.COLUMN_VISITS_ID + " = " + id, null);
    }



    protected void onListItemClick(ListView l, View v, int position, long id) {
        mitemsDao =new ItemsDAO(getApplicationContext());
        Visits visit = ListVisits.get(position);
        int vid = visit.getvId();
        int vschID =visit.getVschID();

        int items_count = mitemsDao.Items_not_finished_count(vid);
        //show_message_sucess(String.valueOf(items_count));
        if(items_count == 0){
            show_message_sucess("Visit already Completed");
        }else {

            String vname = visit.getVname();
            String vplace = visit.getVplace();
            String vtime = visit.getVtime();
            String vaddress = visit.getVaddress();
            String vtelephone = visit.getvtelephone();
            String vlat = visit.getVlat();
            String vlong = visit.getVlong();
            int vstatus = visit.getVstatus();


            Intent intent = new Intent(this, VisitsDetailsActivity.class);
            intent.putExtra("vid", vid);
            intent.putExtra("vschID", vschID);
            intent.putExtra("vname", vname);
            intent.putExtra("vplace", vplace);
            intent.putExtra("vtime", vtime);
            intent.putExtra("vaddress", vaddress);
            intent.putExtra("vtelephone", vtelephone);
            intent.putExtra("vlat", vlat);
            intent.putExtra("vlong", vlong);
            intent.putExtra("vstatus", vstatus);
            intent.putExtra("vstatus", vstatus);
            intent.putExtra("sdate", schedule_date);

            startActivity(intent);
       }


    }

    private void list_all_visits(int schedule_id){

        mvisitsDAO =new VisitsDAO(getApplicationContext());
        ListVisits = mvisitsDAO.getAllvisitsforSchedule(schedule_id);

        ArrayAdapter<Visits> adapter = new ArrayAdapter<Visits>(this, R.layout.schedule_layout_list, R.id.tv_text1, ListVisits);
        setListAdapter(adapter);
    }





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


    private void show_message_sucess(String data){
        new AlertDialog.Builder(this)
                .setTitle("complete!")
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
