package in.tosc.studddin.fragments.people;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import com.parse.ParseFile;

import in.tosc.studddin.ui.ProgressBarCircular;

/**
 * Created by championswimmer on 26/4/15.
 */
public class PeopleListFragment extends Fragment {
    ProgressBarCircular progressBar;

    String currentuseremail = "";
    String currentuserinterests = "";
    String currentuserinstituition = "";
    String currentusername = "";
    String currentuserqualification = "";
    String currentuser = "";

    public PeopleListFragment() {

    }
    protected class EachRow3 {
        String cname = "";
        String cinterests = "";
        String cdistance = "";
        String cqualification = "";
        String cinstituition = "";
        String cusername = "";
        String cauthData = "";

        Bitmap cbmp;
        ParseFile fileObject;
    }

}
