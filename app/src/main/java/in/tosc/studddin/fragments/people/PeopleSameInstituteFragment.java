package in.tosc.studddin.fragments.people;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.tosc.studddin.R;
import in.tosc.studddin.fragments.signon.SignupDataFragment;

public class PeopleSameInstituteFragment extends Fragment {


    String currentuseremail = "";
    String currentuserinterests= "";
    String currentuserinstituition= "";
    String currentusername= "";
    String currentuserqualification= "";
    String currentuser = "";

    EditText search ;

    ArrayList<EachRow3> list3 = new ArrayList<PeopleSameInstituteFragment.EachRow3>();
    EachRow3 each;
    MyAdapter3 q ;
    ListView lv ;



    public PeopleSameInstituteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people_same_institute, container, false);

        search = (EditText) view.findViewById(R.id.people_search);

        lv = (ListView)view.findViewById(R.id.listviewpeople);



        q = new MyAdapter3(getActivity(), 0, list3);
        q.setNotifyOnChange(true);

        loaddata();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_signin_enter,R.anim.anim_signin_exit);

                ViewPerson newFragment = new ViewPerson();

                final Bundle in = new Bundle();
                in.putString("name" , list3.get(i).cname);
                in.putString("institute" , list3.get(i).cinstituition);
                in.putString("qualifications" , list3.get(i).cqualification);
                in.putString("interests" , list3.get(i).cinterests);
                in.putString("distance" , list3.get(i).cdistance);

                newFragment.setArguments(in);

                transaction.replace(R.id.people_pager,newFragment).commit();

            }
        });

        return  view;
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
            final int pos=position;

            if (convertView == null) {
                convertView = inflat.inflate(R.layout.listview_people, null);
                holder = new ViewHolder();
                holder.textname = (TextView) convertView.findViewById(R.id.people_name);
                holder.textinterests = (TextView) convertView.findViewById(R.id.people_interests);
                holder.textinstituition = (TextView) convertView.findViewById(R.id.people_institute);
                holder.textdistance = (TextView) convertView.findViewById(R.id.people_distance);
                holder.textqualification = (TextView) convertView.findViewById(R.id.people_qualification);

                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            EachRow3 row = getItem(position);

            holder.textname.setText(row.cname);
            holder.textinterests.setText(row.cinterests);
            holder.textinstituition.setText(row.cinstituition);
            holder.textdistance.setText(row.cdistance);
            holder.textqualification.setText(row.cqualification);

            return convertView;
        }




        private class ViewHolder {

            TextView textname;
            TextView textinterests;
            TextView textdistance;
            TextView textinstituition;
            TextView textqualification;

        }


        @Override
        public EachRow3 getItem(int position) {
            // TODO Auto-generated method stub
            return list3.get(position);
        }

    }

    private class EachRow3
    {
        String cname;
        String cinterests ;
        String cdistance ;
        String cqualification ;
        String cinstituition ;

    }

    private void loaddata()
    {

        for(int i  =0 ; i<list3.size(); i++)
        {
            list3.remove(each);
        }

        currentuser = ParseUser.getCurrentUser().getUsername();
        String currentuseremail = ParseUser.getCurrentUser().getString("email");
        String currentuserinterests = ParseUser.getCurrentUser().getString("INTERESTS");
        String currentuserinstituition = ParseUser.getCurrentUser().getString("INSTITUTE");
        String currentusername = ParseUser.getCurrentUser().getString("NAME");
        String currentuserqualification = ParseUser.getCurrentUser().getString("QUALIFICATIONS");



        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContains("INSTITUTE", currentuserinstituition);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {

                    for (ParseUser pu : objects) {
                        //access the data associated with the ParseUser using the get method
                        //pu.getString("key") or pu.get("key")

                        if(!pu.getUsername().equals(currentuser))
                        {
                            each = new EachRow3();
                            each.cname = pu.getString("NAME");
                            each.cinterests = pu.getString("INTERESTS");
                            each.cqualification = pu.getString("QUALIFICATIONS");
                            each.cinstituition = pu.getString("INSTITUTE");
//                        each.cdistance = pu.getString("NAME");

                            list3.add(each);
                        }

                    }


                    lv.setAdapter(q);


                    // The query was successful.
                } else {
                    // Something went wrong.
                }
            }
        });




    }




}
