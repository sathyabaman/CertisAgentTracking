package lk.agent.certislanka.certisagenttracking;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.agent.certislanka.certisagenttracking.data.ItemsDAO;
import lk.agent.certislanka.certisagenttracking.data.OfficersDAO;
import lk.agent.certislanka.certisagenttracking.model.Items;
import lk.agent.certislanka.certisagenttracking.model.Officers;


public class Item_Activity extends ListActivity {

    private SQLiteDatabase mDatabase;
    private ItemsDAO mitemsDAO;
    private OfficersDAO mofficersDAO;
    List<Items> ListItems;
    List<Officers> officerItems;
    private int visit_id;
    private String visit_date;
    private String currentdate;
    String officers_display_text = " ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        visit_id = getIntent().getExtras().getInt("visit_id");
        visit_date = getIntent().getExtras().getString("visit_date");
        //currentdate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        currentdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //show_message(String.valueOf(visit_id));

        list_all_items(visit_id);

    }


    private void list_all_items(int visitid){

        mitemsDAO =new ItemsDAO(getApplicationContext());
        ListItems = mitemsDAO.getAllitemsforviews(visitid);
        //ArrayAdapter<Items> adapter = new ArrayAdapter<Items>(this, R.layout.list_schedules_layout, ListItems);



        ArrayAdapter<Items> adapter = new ArrayAdapter<Items>(this,
                R.layout.item_layout_list, R.id.textView5, ListItems);
        setListAdapter(adapter);


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {



        Items items = ListItems.get(position);
        int itm = items.getItm_id();
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("item_id", items.getItm_id());
        intent.putExtra("itm_name", items.getItm_name());
        intent.putExtra("visit_id", visit_id);
        intent.putExtra("visit_date", visit_date);

        if(!visit_date.equals(currentdate)){
            String er_msg = "Sorry you can submit only today's task.";
            show_message(er_msg);
        }else{
            startActivity(intent);
        }

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_item_, menu);
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
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
