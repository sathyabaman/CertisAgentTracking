package lk.agent.certislanka.certisagenttracking.model;

import java.io.Serializable;

/**
 * Created by administrator on 7/27/15.
 */
public class Officers implements Serializable {


    public static final String TAG = "officers";
    private static final long serialVersionUID = -7406082437623008161L;


    private int id;
    private String off_visit_id;
    private String off_itm_code;
    private String off_itm_count;

    public Officers(){}


    public Officers(String off_visit_id, String off_itm_code, String off_itm_count) {
        this.off_visit_id = off_visit_id;
        this.off_itm_code = off_itm_code;
        this.off_itm_count = off_itm_count;
    }



    public void setId(int id) {
        this.id = id;
    }

    public void setOff_visit_id(String off_visit_id) {
        this.off_visit_id = off_visit_id;
    }

    public void setOff_itm_code(String off_itm_code) {
        this.off_itm_code = off_itm_code;
    }

    public void setOff_itm_count(String off_itm_count) {
        this.off_itm_count = off_itm_count;
    }


    public int getId() {
        return id;
    }

    public String getOff_visit_id() {
        return off_visit_id;
    }

    public String getOff_itm_code() {
        return off_itm_code;
    }

    public String getOff_itm_count() {
        return off_itm_count;
    }



    public String toString() {
        String displayitems = this.getOff_itm_code()+" : "+this.getOff_itm_count()+"\n";
        return displayitems;
    }


}
