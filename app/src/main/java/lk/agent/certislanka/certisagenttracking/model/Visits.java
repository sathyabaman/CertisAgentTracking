package lk.agent.certislanka.certisagenttracking.model;

/**
 * Created by administrator on 7/7/15.
 */
import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by administrator on 7/6/15.
 */
public class Visits implements Serializable {


    public static final String TAG = "visits";
    private static final long serialVersionUID = -7406082437623008161L;

    private int vId;
    private int vschID;
    private String vname;
    private String vtime;
    private String vplace;
    private String vaddress;
    private String vtelephone;
    private String vlat;
    private String vlong;
    private int vstatus;


    private String dateStr;
    private String timeStr;


    public Visits() {}


    public Visits(int vId, int vschID, String vname, String vtime, String vplace, String vaddress, String vtelephone, String vlat, String vlong, int vstatus) {
        this.vId = vId;
        this.vschID = vschID;
        this.vname = vname;
        this.vtime = vtime;
        this.vplace = vplace;
        this.vaddress = vaddress;
        this.vtelephone = vtelephone;
        this.vlat = vlat;
        this.vlong = vlong;
        this.vstatus = vstatus;
    }


    public int getvId() {
        return vId;
    }

    public void setvId(int vId) {
        this.vId = vId;
    }

    public int getVschID() {
        return vschID;
    }

    public void setVschID(int vschID) {
        this.vschID = vschID;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getVplace() {
        return vplace;
    }

    public String getVtime() {
        return vtime;
    }

    public void setVtime(String vtime) {
        this.vtime = vtime;
    }

    public void setVplace(String vplace) {
        this.vplace = vplace;
    }

    public String getVaddress() {
        return vaddress;
    }

    public void setVaddress(String vaddress) {
        this.vaddress = vaddress;
    }

    public String getvtelephone(){return vtelephone; }

    public void setvtelephone(String vtelephone){ this.vtelephone = vtelephone; }

    public String getVlat() {
        return vlat;
    }

    public void setVlat(String vlat) {
        this.vlat = vlat;
    }

    public String getVlong() {
        return vlong;
    }

    public void setVlong(String vlong) {
        this.vlong = vlong;
    }

    public int getVstatus() {
        return vstatus;
    }

    public void setVstatus(int vstatus) {
        this.vstatus = vstatus;
    }



    @Override
    public String toString() {
        String new_name_string;
        String new_place_string;

        int name_length = this.getVname().length();
        if(name_length > 18){
           new_name_string = this.getVname().substring(0, 18);
            new_name_string = new_name_string+"...";
        }else{
           new_name_string = this.getVname();
        }

        int place_length = this.getVplace().length();
        if(place_length > 18){
            new_place_string = this.getVplace().substring(0, 18);
            new_place_string = new_name_string+"...";
        }else{
            new_place_string = this.getVplace();
        }



        String date = this.getVtime();
        String[] time = date.split(" ");
        String displayvisits = "Company :  "+new_name_string+"\nCustomer :  "+new_place_string+"\nVisit Date :  "+time[0]+"\nVisit Time :  "+time[1]+" "+time[2];
        return displayvisits;
    }





}
