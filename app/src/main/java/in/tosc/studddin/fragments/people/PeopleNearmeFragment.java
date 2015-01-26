package in.tosc.studddin.fragments.people;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.ArrayList;

import in.tosc.studddin.R;

public class PeopleNearmeFragment extends Fragment {



    EditText search ;

    ArrayList<EachRow3> list3 = new ArrayList<PeopleNearmeFragment.EachRow3>();
    EachRow3 each;
    MyAdapter3 q ;
    ListView lv ;



    public PeopleNearmeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people_nearme, container, false);


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
                transaction.setCustomAnimations(R.anim.anim_signin_enter, R.anim.anim_signin_exit);

                ViewPerson newFragment = new ViewPerson();

                final Bundle in = new Bundle();
                in.putString("name", list3.get(i).cname);
                in.putString("institute", list3.get(i).cinstituition);
                in.putString("qualifications" , list3.get(i).cqualification);
                in.putString("interests" , list3.get(i).cinterests);
                in.putString("distance" , list3.get(i).cdistance);

                newFragment.setArguments(in);

                transaction.replace(R.id.peopleNearme_container,newFragment).addToBackStack(null).commit();


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


        ParseUser.getCurrentUser().getUsername();


        for(int i=0 ; i<list3.size(); i++)
        {
            list3.remove(each);
        }


        for(int  i = 0 ; i<15; i++)
        {
            each = new EachRow3();
            each.cname = "Laavanye";
            each.cinterests  = "BasketBAll "  ;
            each.cqualification  = "B tech"  ;
            each.cinstituition  = "DTU"  ;
            each.cdistance = "5 km"  ;

            list3.add(each);
        }

        lv.setAdapter(q);


    }



}
