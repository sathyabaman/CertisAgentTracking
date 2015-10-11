package lk.agent.certislanka.certisagenttracking.model;

/**
 * Created by administrator on 7/10/15.
 */
import java.io.Serializable;
public class Items implements Serializable{

    public static final String TAG = "items";
    private static final long serialVersionUID = -7406082437623008161L;

    private int itm_id;
    private int itm_vst_id;
    private String itm_name;
    private int itm_chk_sts;
    private String itm_commnt;
    private String itm_lat;
    private String itm_lng;
    private String itm_time;
    private int itm_rmo_sts;

    public Items(){}

    public Items(int itm_id, int itm_vst_id, String itm_name, int itm_chk_sts, String itm_commnt, String itm_lat, String itm_lng, String itm_time, int itm_rmo_sts) {
        this.itm_id = itm_id;
        this.itm_vst_id = itm_vst_id;
        this.itm_name = itm_name;
        this.itm_chk_sts = itm_chk_sts;
        this.itm_commnt = itm_commnt;
        this.itm_lat = itm_lat;
        this.itm_lng = itm_lng;
        this.itm_time = itm_time;
        this.itm_rmo_sts = itm_rmo_sts;
    }



    public int getItm_id() {
        return itm_id;
    }

    public int getItm_vst_id() {
        return itm_vst_id;
    }

    public String getItm_name() {
        return itm_name;
    }

    public int getItm_chk_sts() {
        return itm_chk_sts;
    }

    public String getItm_commnt() {
        return itm_commnt;
    }

    public String getItm_lat() {
        return itm_lat;
    }

    public String getItm_lng() {
        return itm_lng;
    }

    public String getItm_time() {
        return itm_time;
    }
    public int getItm_rmo_sts() {
        return itm_rmo_sts;
    }




    public void setItm_id(int itm_id) {
        this.itm_id = itm_id;
    }

    public void setItm_vst_id(int itm_vst_id) {
        this.itm_vst_id = itm_vst_id;
    }

    public void setItm_name(String itm_name) {
        this.itm_name = itm_name;
    }

    public void setItm_chk_sts(int itm_chk_sts) {
        this.itm_chk_sts = itm_chk_sts;
    }

    public void setItm_commnt(String itm_commnt) {
        this.itm_commnt = itm_commnt;
    }

    public void setItm_lat(String itm_lat) {
        this.itm_lat = itm_lat;
    }

    public void setItm_lng(String itm_lng) {
        this.itm_lng = itm_lng;
    }

    public void setItm_time(String itm_time) {
        this.itm_time = itm_time;
    }
    public void setItm_rmo_sts(int itm_rmo_sts) {
        this.itm_rmo_sts = itm_rmo_sts;
    }




    public String toString() {
        String displayitems = this.getItm_name();
        return displayitems;
    }




}
