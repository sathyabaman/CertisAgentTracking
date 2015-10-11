package lk.agent.certislanka.certisagenttracking.data;

import android.app.Application;

/**
 * Created by administrator on 7/8/15.
 */
public class Constants extends Application {

    private String apibaseurl = "http://45.40.163.175/web/index.php/api/rest";

    public String getGlobalapibaseurl() {
        return apibaseurl;
    }

    public void setGlobalapibaseurl(String str) {
        apibaseurl = str;
    }
}
