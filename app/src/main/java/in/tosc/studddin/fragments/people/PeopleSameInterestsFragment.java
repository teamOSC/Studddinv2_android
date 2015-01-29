package in.tosc.studddin.fragments.people;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import in.tosc.studddin.R;


public class PeopleSameInterestsFragment extends Fragment {

    ProgressBar progressBar;

    HashMap<String, Boolean> existingelement = new HashMap<String, Boolean>();

    String currentuseremail = "";
    String currentuserinterests = "";
    String currentuserinstituition = "";
    String currentusername = "";
    String currentuserqualification = "";
    String currentuser = "";

    EditText search;

    ArrayList<EachRow3> list3 = new ArrayList<PeopleSameInterestsFragment.EachRow3>();
    EachRow3 each;
    MyAdapter3 q;
    ListView lv;


    public PeopleSameInterestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people_same_interests, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_people);
        search = (EditText) view.findViewById(R.id.people_search);
        lv = (ListView) view.findViewById(R.id.listviewpeople);

        q = new MyAdapter3(getActivity(), 0, list3);
        q.setNotifyOnChange(true);

        loaddata();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ViewPerson newFragment = new ViewPerson();

                String tname = list3.get(i).cname;
                String tinterests = list3.get(i).cinterests;
                String tinstitute =  list3.get(i).cinstituition;
                String tqualifications = list3.get(i).cqualification;
                String tdistance = list3.get(i).cdistance;

                if(tname==null)
                    tname= " - " ;
                if(tinterests==null)
                    tinterests= " - " ;
                if(tinstitute==null)
                    tinstitute= " - " ;
                if(tqualifications==null)
                    tqualifications= " - " ;
                if(tdistance==null)
                    tdistance= " - " ;

                final Bundle in = new Bundle();
                in.putString("name", tname);
                in.putString("institute", tinterests);
                in.putString("qualifications" , tinstitute);
                in.putString("interests" , tqualifications);
                in.putString("distance" , tdistance);

                newFragment.setArguments(in);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_signin_enter,R.anim.anim_signin_exit);

                transaction.replace(R.id.container,newFragment).addToBackStack("PeopleNearMe").commit();
            }
        });

        return view;
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

            if(row.fileObject!=null)
            {
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

                                    Log.d("test",
                                            "There was a problem downloading the data.");
                                }
                            }
                        });
            }

            else
            {
                holder.userimg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_person));
            }

            return convertView;
        }


        private class ViewHolder {

            TextView textname;
            TextView textinterests;
            TextView textdistance;
            TextView textinstituition;
            TextView textqualification;
            ParseImageView userimg;

        }


        @Override
        public EachRow3 getItem(int position) {
            // TODO Auto-generated method stub
            return list3.get(position);
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


    private void loaddata() {

        for (int i = 0; i < list3.size(); i++) {
            list3.remove(each);
        }


        currentuser = ParseUser.getCurrentUser().getUsername();
        currentuseremail = ParseUser.getCurrentUser().getString("email");
        currentuserinterests = ParseUser.getCurrentUser().getString("INTERESTS");
        currentuserinstituition = ParseUser.getCurrentUser().getString("INSTITUTE");
        currentusername = ParseUser.getCurrentUser().getString("NAME");
        currentuserqualification = ParseUser.getCurrentUser().getString("QUALIFICATIONS");

        if (currentuserinterests == null) {
            currentuserinterests = "";
        }
        List<String> interestslist = Arrays.asList(currentuserinterests.split(", "));

        for (int c = 0; c < interestslist.size(); c++) {
            if (!interestslist.get(c).equals("") || !interestslist.get(c).equals(null)) {


                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereContains("INTERESTS", interestslist.get(c));
                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {

                            for (ParseUser pu : objects) {
                                //access the data associated with the ParseUser using the get method
                                //pu.getString("key") or pu.get("key")

                                if (!pu.getUsername().equals(currentuser)) {

                                    if (!existingelement.containsKey(pu.getUsername())) {

                                        each = new EachRow3();
                                        each.cname = pu.getString("NAME");
                                        each.cinterests = pu.getString("INTERESTS");
                                        each.cqualification = pu.getString("QUALIFICATIONS");
                                        each.cinstituition = pu.getString("INSTITUTE");
//                                          each.cdistance = pu.getString("NAME");
                                        each.cusername = pu.getString("username");
                                        try
                                        {
                                            each.fileObject = (ParseFile) pu.get("image");

                                        }
                                        catch (Exception e1 )
                                        {
                                            System.out.print("nahh");
                                        }



                                        list3.add(each);
                                        existingelement.put(pu.getUsername(), true);
                                    }
                                }
                            }

                            // The query was successful.
                        } else {
                            // Something went wrong.
                        }

                        lv.setAdapter(q);
                        progressBar.setVisibility(View.GONE);
                        lv.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }
}
