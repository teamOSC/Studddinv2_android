package in.tosc.studddin.fragments.people;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.ui.ParseCircularImageView;
import in.tosc.studddin.ui.ProgressBarCircular;

public class PeopleNearmeFragment extends Fragment {

    ProgressBarCircular progressBar;
    Dialog dialogPeople;

    String currentuseremail = "";
    String currentuserinterests = "";
    String currentuserinstituition = "";
    String currentusername = "";
    String currentuserqualification = "";
    String currentuser = "";
    ParseGeoPoint userlocation = new ParseGeoPoint(0, 0);


    EditText search;

    ArrayList<EachRow3> list3 = new ArrayList<EachRow3>();
    EachRow3 each;
    MyAdapter3 q;
    ListView lv;


    public PeopleNearmeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people_nearme, container, false);
        progressBar = (ProgressBarCircular) view.findViewById(R.id.progressbar_people);
        progressBar.setBackgroundColor(getResources().getColor(R.color.pink));

        search = (EditText) view.findViewById(R.id.people_search);

        lv = (ListView) view.findViewById(R.id.listviewpeople);

        loaddata();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                loaddataAfterSearch( editable.toString());
            }
        });


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

    private void loaddata() {

      list3.clear();


        currentuser = ParseUser.getCurrentUser().getUsername();
        currentuseremail = ParseUser.getCurrentUser().getString(ParseTables.Users.EMAIL);
        currentuserinterests = ParseUser.getCurrentUser().getString(ParseTables.Users.INTERESTS);
        currentuserinstituition = ParseUser.getCurrentUser().getString(ParseTables.Users.INSTITUTE);
        currentusername = ParseUser.getCurrentUser().getString(ParseTables.Users.NAME);
        currentuserqualification = ParseUser.getCurrentUser().getString(ParseTables.Users.QUALIFICATIONS);
        userlocation = ParseUser.getCurrentUser().getParseGeoPoint(ParseTables.Users.LOCATION);


        // DUMMY DATA SO THAT IT DISPLAYS SOMETHING
        if (userlocation==null ||  userlocation.getLatitude() == 0)
        {
            userlocation = new ParseGeoPoint(28.7434552 , 77.1205612);
        }



        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNear(ParseTables.Users.LOCATION, userlocation);

        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {


                    for (ParseUser pu : objects) {
                        //access the data associated with the ParseUser using the get method
                        //pu.getString("key") or pu.get("key")

                        if (!pu.getUsername().equals(currentuser)) {


                            each = new EachRow3();
                            each.cname = pu.getString(ParseTables.Users.NAME);
                            each.cinterests = pu.getString(ParseTables.Users.INTERESTS);
                            each.cqualification = pu.getString(ParseTables.Users.QUALIFICATIONS);
                            each.cinstituition = pu.getString(ParseTables.Users.INSTITUTE);
//                                          each.cdistance = pu.getString(ParseTables.Users.NAME);
                            each.cusername = pu.getString(ParseTables.Users.USERNAME);
                            ParseGeoPoint temploc = pu.getParseGeoPoint(ParseTables.Users.LOCATION);
                            if (temploc != null && temploc.getLatitude() != 0) {
                                if (userlocation != null) {
                                    each.cdistance = String.valueOf((int) temploc.distanceInKilometersTo(userlocation)) + " km";
                                } else {
                                    each.cdistance = "N/A";
                                }
                            } else {
                                each.cdistance = "N/A";
                            }

                            try {
                                each.fileObject = (ParseFile) pu.get(ParseTables.Users.IMAGE);
                            } catch (Exception e1) {
                                System.out.print("nahh");
                            }

                            list3.add(each);


                        }
                    }

                    // The query was successful.
                } else {
                    // Something went wrong.
                }

                q = new MyAdapter3(getActivity(), 0, list3);
                q.notifyDataSetChanged();

                lv.setAdapter(q);
                progressBar.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
            }
        });

    }

    private void loaddataAfterSearch(String textSearch) {

        list3.clear();


        currentuser = ParseUser.getCurrentUser().getUsername();
        currentuseremail = ParseUser.getCurrentUser().getString(ParseTables.Users.EMAIL);
        currentuserinterests = ParseUser.getCurrentUser().getString(ParseTables.Users.INTERESTS);
        currentuserinstituition = ParseUser.getCurrentUser().getString(ParseTables.Users.INSTITUTE);
        currentusername = ParseUser.getCurrentUser().getString(ParseTables.Users.NAME);
        currentuserqualification = ParseUser.getCurrentUser().getString(ParseTables.Users.QUALIFICATIONS);
        userlocation = ParseUser.getCurrentUser().getParseGeoPoint(ParseTables.Users.LOCATION);

 // DUMMY DATA SO THAT IT DISPLAYS SOMETHING
        if (userlocation==null ||  userlocation.getLatitude() == 0)
        {
           userlocation = new ParseGeoPoint(28.7434552 , 77.1205612);
        }


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNear(ParseTables.Users.LOCATION, userlocation);
        query.whereMatches(ParseTables.Users.NAME, "(" + textSearch + ")", "i");


        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {


                    for (ParseUser pu : objects) {
                        //access the data associated with the ParseUser using the get method
                        //pu.getString("key") or pu.get("key")

                        if (!pu.getUsername().equals(currentuser)) {


                            each = new EachRow3();
                            each.cname = pu.getString(ParseTables.Users.NAME);
                            each.cinterests = pu.getString(ParseTables.Users.INTERESTS);
                            each.cqualification = pu.getString(ParseTables.Users.QUALIFICATIONS);
                            each.cinstituition = pu.getString(ParseTables.Users.INSTITUTE);
//                                          each.cdistance = pu.getString(ParseTables.Users.NAME);
                            each.cusername = pu.getString(ParseTables.Users.USERNAME);
                            ParseGeoPoint temploc = pu.getParseGeoPoint(ParseTables.Users.LOCATION);
                            if (temploc != null && temploc.getLatitude() != 0) {
                                if (userlocation != null) {
                                    each.cdistance = String.valueOf((int) temploc.distanceInKilometersTo(userlocation)) + " km";
                                } else {
                                    each.cdistance = "N/A";
                                }
                            } else {
                                each.cdistance = "N/A";
                            }

                            try {
                                each.fileObject = (ParseFile) pu.get(ParseTables.Users.IMAGE);
                            } catch (Exception e1) {
                                System.out.print("nahh");
                            }

                            list3.add(each);


                        }
                    }

                    // The query was successful.
                } else {
                    // Something went wrong.
                }

                q = new MyAdapter3(getActivity(), 0, list3);
                q.notifyDataSetChanged();

                lv.setAdapter(q);
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
                holder.textinstituition = (TextView) convertView.findViewById(R.id.people_institute);
                holder.textdistance = (TextView) convertView.findViewById(R.id.people_distance);
                holder.textqualification = (TextView) convertView.findViewById(R.id.people_qualification);
                holder.userimg = (ParseCircularImageView) convertView.findViewById(R.id.people_userimg);


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
                                    Log.d("test", "There was a problem downloading the data.");
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
