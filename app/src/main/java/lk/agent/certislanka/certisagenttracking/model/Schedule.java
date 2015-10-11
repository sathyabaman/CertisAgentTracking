package lk.agent.certislanka.certisagenttracking.model;

/**
 * Created by administrator on 7/7/15.
 */
import java.io.Serializable;

/**
 * Created by administrator on 7/6/15.
 */
public class Schedule implements Serializable {

    public static final String TAG = "Schedule";
    private static final long serialVersionUID = -7406082437623008161L;

    private int sId;
    private String sName;
    private String SDate;

    public Schedule() {}


    public Schedule(int id, String name, String date) {
        this.sId = id;
        this.sName = name;
        this.SDate = date;
    }


    public int getId() {
        return sId;
    }
    public void setId(int sId) {
        this.sId = sId;
    }
    public String getName() {
        return sName;
    }
    public void setName(String sName) {
        this.sName = sName;
    }

    public String getDate() {
        return SDate;
    }
    public void setDate(String SDate) {
        this.SDate = SDate;
    }


    @Override
    public String toString() {
        String displayvalue;

        if((this.getName()).equals("Friday")) {
            displayvalue = "\n"+this.getName()+"\t \t \t \t \t \t \t"+this.getDate();
        }else if((this.getName()).equals("Monday")){
            displayvalue = "\n"+this.getName()+"\t \t \t \t \t \t"+this.getDate();
        }else if((this.getName()).equals("Tuesday")){
            displayvalue = "\n"+this.getName()+"\t \t \t \t \t \t"+this.getDate();
        }else if((this.getName()).equals("Sunday")){
            displayvalue = "\n"+this.getName() +"\t \t \t \t \t \t"+this.getDate();
        }else if((this.getName()).equals("Wednesday")){
            displayvalue = "\n"+this.getName()+"\t \t \t \t"+this.getDate();
        }else{
            displayvalue = "\n"+this.getName()+"\t \t \t \t \t" +this.getDate();
        }

        return displayvalue;
    }
}
