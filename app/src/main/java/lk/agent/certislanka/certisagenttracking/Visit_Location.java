package lk.agent.certislanka.certisagenttracking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import org.w3c.dom.Document;

import java.util.ArrayList;

import lk.agent.certislanka.certisagenttracking.data.GMapV2Direction;
import lk.agent.certislanka.certisagenttracking.data.GPSTracker;



public class Visit_Location extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    GPSTracker gps;
    GMapV2Direction md;


    static final double _eQuatorialEarthRadius = 6378.1370D;
    static final double _d2r = (Math.PI / 180D);
    private String dest_lat;
    private String dest_lon;
    private String dest_place;

    private double mylatitude;
    private double mylongitude;
    private double distanceinkm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit__location);
        getActionBar().setDisplayHomeAsUpEnabled(true);


       dest_lat   = getIntent().getExtras().getString("latitu");
       dest_lon    = getIntent().getExtras().getString("longit");
       dest_place = getIntent().getExtras().getString("dest_loc");

        setUpMapIfNeeded();

        //draw road directions
        if (!isNetworkAvailable()) {
            show_message("No internet connection. Directions cannot be viewed!");
        } else {

        show_my_gps_location();

        double distanceinmeters = (int) (1000D * HaversineInKM(dest_lat, dest_lon, mylatitude, mylongitude));
        distanceinkm = distanceinmeters/1000;
            // Toast.makeText(getApplicationContext(), "Distance approximately " +Double.toString(distanceinkm)+" KM", Toast.LENGTH_LONG).show();

        gotoLocation(dest_lat, dest_lon, 11, dest_place);


            //Checking if GPS is on and showing Directions
            if(gps.canGetLocation()) {
                draw_road_directions(dest_lat, dest_lon, mylatitude, mylongitude);
            }else{ }
        }


        Button bt_goole = (Button) findViewById(R.id.btn_v_google_map);
        bt_goole.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?   saddr=" + mylatitude + "," + mylongitude + "&daddr=" + dest_lat + "," + dest_lon));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }




    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

       // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Destination"));
    }

    private void gotoLocation(String lat, String lng,
                              float zoom, String dest_place) {
        double latitude = Double.parseDouble( lat.replace(".",".") );
        double longitude = Double.parseDouble(lng.replace(".", "."));


        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Destination : " + dest_place).snippet("Distance approximately : "+Double.toString(distanceinkm)+" KM");
       // marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_orange));
        mMap.addMarker(marker);
        LatLng ll = new LatLng(latitude, longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);
    }



    private void show_my_gps_location(){
        gps = new GPSTracker(Visit_Location.this);
        // check if GPS enabled
        if(gps.canGetLocation()){

            mylatitude = gps.getLatitude();
            mylongitude = gps.getLongitude();

            MarkerOptions marker = new MarkerOptions().position(new LatLng(mylatitude, mylongitude)).title("My Location");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_current_location));
            mMap.addMarker(marker);

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
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


    public static double HaversineInKM(String lati1, String longi1, double lat2, double long2) {
        double lat1 = Double.parseDouble(String.valueOf(lati1));
        double long1 = Double.parseDouble(String.valueOf(longi1));
        double dlong = (long2 - long1) * _d2r;
        double dlat = (lat2 - lat1) * _d2r;
        double a = Math.pow(Math.sin(dlat / 2D), 2D) + Math.cos(lat1 * _d2r) * Math.cos(lat2 * _d2r)
                * Math.pow(Math.sin(dlong / 2D), 2D);
        double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
        double d = _eQuatorialEarthRadius * c;
        return d;
    }



    public void draw_road_directions(String lati1, String longi1, double lat2, double long2){
        double lat1 = Double.parseDouble(String.valueOf(lati1));
        double long1 = Double.parseDouble(String.valueOf(longi1));

        LatLng fromPosition = new LatLng(lat1, long1);
        LatLng toPosition = new LatLng(lat2, long2);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        md = new GMapV2Direction();
        mMap = ((SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        //location for opening the camera
        LatLng coordinates = new LatLng(lat1, long1);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 11));

        //mMap.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
       // mMap.addMarker(new MarkerOptions().position(toPosition).title("End"));

        Document doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);
        int duration = md.getDurationValue(doc);
        String distance = md.getDistanceText(doc);
        String start_address = md.getStartAddress(doc);
        String copy_right = md.getCopyRights(doc);

        ArrayList<LatLng> directionPoint = md.getDirection(doc);
        PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.DKGRAY);

        for(int i = 0 ; i < directionPoint.size() ; i++) {
            rectLine.add(directionPoint.get(i));
        }

        mMap.addPolyline(rectLine);
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
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
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



}
