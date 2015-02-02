package in.tosc.studddin.fragments.people;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.tosc.studddin.R;
import in.tosc.studddin.utils.ProgressBarCircular;


public class PeopleSameInterestsFragment extends Fragment {

    ProgressBarCircular progressBar;

    HashMap<String, Boolean> existingelement = new HashMap<String, Boolean>();

    ParseUser currentUser = ParseUser.getCurrentUser();

    String currentEmail = "";
    String currentInstitution = "";
    String currentUsername = "";
    String currentName = "";
    String currentQualification = "";
    ParseGeoPoint currentLocation;
    ParseGeoPoint userlocation = new ParseGeoPoint(0, 0);

    EditText search;

    ArrayList<EachRow3> list3 = new ArrayList<PeopleSameInterestsFragment.EachRow3>();
    EachRow3 each;
    MyAdapter3 q;
    ListView lv;

    private static final String TAG = "PeopleSameInterestsFragment";

    public PeopleSameInterestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people_same_interests, container, false);

        progressBar = (ProgressBarCircular) view.findViewById(R.id.progressbar_people);
        progressBar.setBackgroundColor(getResources().getColor(R.color.pink));
        search = (EditText) view.findViewById(R.id.people_search);
        lv = (ListView) view.findViewById(R.id.listviewpeople);

        q = new MyAdapter3(getActivity(), 0, list3);
        lv.setAdapter(q);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } ,"SameInterestQuery").start();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ViewPerson newFragment = new ViewPerson();

                String tname = list3.get(i).cname;
                String tinterests = list3.get(i).cinterests;
                String tinstitute = list3.get(i).cinstituition;
                String tqualifications = list3.get(i).cqualification;
                String tdistance = list3.get(i).cdistance;
                ParseFile tfile = list3.get(i).fileObject;

                final Bundle in = new Bundle();


                if (tname == null)
                    tname = " - ";
                if (tinterests == null)
                    tinterests = " - ";
                if (tinstitute == null)
                    tinstitute = " - ";
                if (tqualifications == null)
                    tqualifications = " - ";
                if (tdistance == null)
                    tdistance = " - ";


                in.putString("name", tname);
                in.putString("institute", tinstitute);
                in.putString("qualifications", tqualifications);
                in.putString("interests", tinterests);
                in.putString("distance", tdistance);

                newFragment.setArguments(in);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_signin_enter, R.anim.anim_signin_exit);


                if (tfile != null) {
                    tfile
                            .getDataInBackground(new GetDataCallback() {

                                public void done(byte[] data,
                                                 ParseException e) {
                                    if (e == null) {
                                        Log.d("test",
                                                "We've got data in data.");

                                        in.putByteArray("pic", data);
                                        System.out.print("pic3" + String.valueOf(data));
                                        transaction.replace(R.id.container, newFragment).addToBackStack("PeopleNearMe").commit();


                                    } else {
                                        Log.d("test", "There was a problem downloading the data.");
                                    }
                                }
                            });
                } else {


                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_person);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();

                    in.putByteArray("pic", bitmapdata);
                    System.out.print("pic2" + String.valueOf(bitmapdata));

                    transaction.replace(R.id.container, newFragment).addToBackStack("PeopleNearMe").commit();

                }

            }
        });

        return view;
    }

    private void loadData() throws ParseException {

        currentUsername = ParseUser.getCurrentUser().getUsername();
        currentEmail = ParseUser.getCurrentUser().getString("email");
        currentInstitution = ParseUser.getCurrentUser().getString("INSTITUTE");
        currentName = ParseUser.getCurrentUser().getString("NAME");
        currentQualification = ParseUser.getCurrentUser().getString("QUALIFICATIONS");
        currentLocation = ParseUser.getCurrentUser().getParseGeoPoint("location");

        Set<ParseUser> sameInterestUsers = new HashSet();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Interests");
        query.whereEqualTo("users", currentUser);
        List<ParseObject> currentInterests = query.find();
        for (ParseObject interest : currentInterests) {
            ParseQuery relationQuery = interest.getRelation("users").getQuery();
            relationQuery.whereNotEqualTo("username", currentUser.getUsername());
            List<ParseUser> parseUsers = relationQuery.find();
            for (ParseUser parseUser : parseUsers) {
                sameInterestUsers.add(parseUser);
            }
        }

        for (ParseUser parseUser : sameInterestUsers) {
            each = new EachRow3();
            each.cname = parseUser.getString("NAME");
            each.cinterests = parseUser.getString("INTERESTS");
            each.cqualification = parseUser.getString("QUALIFICATIONS");
            each.cinstituition = parseUser.getString("INSTITUTE");
            each.cusername = parseUser.getString("username");
            ParseGeoPoint temploc = parseUser.getParseGeoPoint("location");
            if (temploc != null && temploc.getLatitude() != 0) {
                if (userlocation != null) {
                    each.cdistance = String.valueOf((int) temploc.distanceInKilometersTo(userlocation)) + " km";
                } else {
                    each.cdistance = "13 km";
                }
            } else {
                each.cdistance = "16 km";
            }

            try {
                each.fileObject = (ParseFile) parseUser.get("image");
            } catch (Exception e) {
                e.printStackTrace();
            }
            list3.add(each);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                q.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
            }
        });
    }

    class MyAdapter3 extends ArrayAdapter<EachRow3> {
        LayoutInflater inflat;
        ViewHolder holder;

        public MyAdapter3(Context context, int textViewResourceId,
                          ArrayList<EachRow3> objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
            inflat = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final int pos = position;

            if (convertView == null) {
                convertView = inflat.inflate(R.layout.listview_people, null);
                holder = new ViewHolder();
                holder.textname = (TextView) convertView.findViewById(R.id.people_name);
                holder.textinterests = (TextView) convertView.findViewById(R.id.people_interests);
//                holder.textdate = (TextView) convertView.findViewById(R.id.date);
                holder.textinstituition = (TextView) convertView.findViewById(R.id.people_institute);
                holder.textdistance = (TextView) convertView.findViewById(R.id.people_distance);
                holder.textqualification = (TextView) convertView.findViewById(R.id.people_qualification);
                holder.userimg = (ParseImageView) convertView.findViewById(R.id.people_userimg);


                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            EachRow3 row = getItem(position);

            holder.textname.setText(row.cname);
            holder.textinterests.setText(row.cinterests);
            holder.textinstituition.setText(row.cinstituition);
            holder.textdistance.setText(row.cdistance);
            holder.textqualification.setText(row.cqualification);
            holder.textdistance.setText(row.cdistance);


//            Toast.makeText(getActivity(), row.cusername, Toast.LENGTH_SHORT).show();

            if (row.fileObject != null) {
                row.fileObject
                        .getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data,
                                             ParseException e) {
                                if (e == null) {
                                    Log.d("test",
                                            "We've got data in data.");

                                    holder.userimg.setImageBitmap(BitmapFactory
                                            .decodeByteArray(
                                                    data, 0,
                                                    data.length));

                                } else {
                                    Log.e("test", "There was a problem downloading the data.");
                                }
                            }
                        });
            } else {
                holder.userimg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_person));
            }

            return convertView;
        }

        @Override
        public EachRow3 getItem(int position) {
            // TODO Auto-generated method stub
            return list3.get(position);
        }

        private class ViewHolder {

            TextView textname;
            TextView textinterests;
            TextView textdistance;
            TextView textinstituition;
            TextView textqualification;
            ParseImageView userimg;

        }

    }

    private class EachRow3 {
        String cname;
        String cinterests;
        String cdistance;
        String cqualification;
        String cinstituition;
        String cusername;
        Bitmap cbmp;
        ParseFile fileObject;
    }
}
