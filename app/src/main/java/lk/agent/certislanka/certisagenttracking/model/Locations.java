package lk.agent.certislanka.certisagenttracking.model;

import java.io.Serializable;

/**
 * Created by administrator on 7/21/15.
 */
public class Locations implements Serializable {

    public static final String TAG = "locations";
    private static final long serialVersionUID = -7406082437623008161L;


    private int id;
    private String key;
    private String agent_bg_location_lat;
    private String agent_bg_location_lng;
    private String agent_bg_battery;
    private String agent_bg_app_date;
    private String gps_key;


    public Locations(){}


    public Locations(int id, String key, String agent_bg_location_lat, String agent_bg_location_lng, String agent_bg_battery, String agent_bg_app_date, String gps_key) {
        this.id = id;
        this.key = key;
        this.agent_bg_location_lat = agent_bg_location_lat;
        this.agent_bg_location_lng = agent_bg_location_lng;
        this.agent_bg_battery = agent_bg_battery;
        this.agent_bg_app_date = agent_bg_app_date;
        this.gps_key = gps_key;
    }


    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getAgent_bg_location_lat() {
        return agent_bg_location_lat;
    }

    public String getAgent_bg_location_lng() {
        return agent_bg_location_lng;
    }

    public String getAgent_bg_battery() {
        return agent_bg_battery;
    }

    public String getAgent_bg_app_date() {
        return agent_bg_app_date;
    }

    public String getGps_key() {
        return gps_key;
    }



    public void setId(int id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setAgent_bg_location_lat(String agent_bg_location_lat) {
        this.agent_bg_location_lat = agent_bg_location_lat;
    }

    public void setAgent_bg_location_lng(String agent_bg_location_lng) {
        this.agent_bg_location_lng = agent_bg_location_lng;
    }

    public void setAgent_bg_battery(String agent_bg_battery) {
        this.agent_bg_battery = agent_bg_battery;
    }

    public void setAgent_bg_app_date(String agent_bg_app_date) {
        this.agent_bg_app_date = agent_bg_app_date;
    }

    public void setGps_key(String gps_key) {
        this.gps_key = gps_key;
    }

    public String toString() {
        String displaylocation = "\nTask : "+this.getAgent_bg_location_lat();
        return displaylocation;
    }

}
