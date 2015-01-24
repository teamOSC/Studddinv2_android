package in.tosc.studddin.fragments.people;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import in.tosc.studddin.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PeopleSameInstituteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PeopleSameInstituteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeopleSameInstituteFragment extends Fragment {

//    ArrayList<EachRow> list3 = new ArrayList<PeopleNearmeFragment.EachRow>();
//    EachRow each;
//    MyAdapter q ;
//    ListView lv ;

    ArrayList<String> namelist = new ArrayList<String>();
    ArrayList<String> institutelist = new ArrayList<String>();
    ArrayList<String> qualificationlist = new ArrayList<String>();
    ArrayList<String> arealist = new ArrayList<String>();
    ArrayList<String> distancelist = new ArrayList<String>();

    public PeopleSameInstituteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people_same_institute, container, false);
    }


}
