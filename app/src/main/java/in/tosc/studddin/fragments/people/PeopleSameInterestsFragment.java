package in.tosc.studddin.fragments.people;

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

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.tosc.studddin.ApplicationWrapper;
import in.tosc.studddin.R;
import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.ui.ParseCircularImageView;
import in.tosc.studddin.ui.ProgressBarCircular;
import in.tosc.studddin.utils.Utilities;


public class PeopleSameInterestsFragment extends Fragment {

    ProgressBarCircular progressBar;


    String currentuseremail = "";
    String currentuserinterests = "";
    String currentuserinstituition = "";
    String currentusername = "";
    String currentuserqualification = "";
    String currentuser = "";
    String currentuserlocation = "";
    ParseGeoPoint userlocation = new ParseGeoPoint(0, 0);

    EditText search;

    ParseUser User = ParseUser.getCurrentUser();


    ArrayList<EachRow3> listOfPeople = new ArrayList<PeopleSameInterestsFragment.EachRow3>();
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
        progressBar.setBackgroundColor(getResources().getColor(R.color.peopleColorPrimaryDark));
        search = (EditText) view.findViewById(R.id.people_search);
        lv = (ListView) view.findViewById(R.id.listviewpeople);

        if (Utilities.isNetworkAvailable(getActivity()))
            loaddata(false);
        else
            loaddata(true);



        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // ALWAYS SEARCH FROM CACHE
                loaddataAfterSearch(editable.toString(),true);
       }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ViewPerson newFragment = new ViewPerson();

                String tname = listOfPeople.get(i).cname;
                String tinterests = listOfPeople.get(i).cinterests;
                String tinstitute = listOfPeople.get(i).cinstituition;
                String tqualifications = listOfPeople.get(i).cqualification;
                String tdistance = listOfPeople.get(i).cdistance;
                String tusername = listOfPeople.get(i).cusername;
                String tauthData = listOfPeople.get(i).cauthData;

                ParseFile tfile = listOfPeople.get(i).fileObject;

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
                 if (tusername == null)
                    tusername = " - ";
                 if (tauthData == null)
                    tauthData = " - ";

                in.putString("name", tname);
                in.putString("institute", tinstitute);
                in.putString("qualifications", tqualifications);
                in.putString("interests", tinterests);
                in.putString("distance", tdistance);
                in.putString("username", tusername);
                in.putString("authData", tauthData);

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
                                        if(ApplicationWrapper.LOG_DEBUG) Log.d("test",
                                                "We've got data in data.");

                                        in.putByteArray("pic", data);
                                        System.out.print("pic3" + String.valueOf(data));
                                        transaction.add(R.id.container, newFragment).hide(PeopleSameInterestsFragment.this).addToBackStack(PeopleSameInterestsFragment.class.getName()).commit();


                                    } else {
                                        if(ApplicationWrapper.LOG_DEBUG) Log.d("test", "There was a problem downloading the data.");
                                    }
                                }
                            });
                } else {


                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_picture_blank_portrait);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();

                    in.putByteArray("pic", bitmapdata);
                    System.out.print("pic2" + String.valueOf(bitmapdata));

                    transaction.add(R.id.container, newFragment).hide(PeopleSameInterestsFragment.this).addToBackStack(PeopleSameInterestsFragment.class.getName()).commit();

                }

            }
        });

        return view;
    }

    private void loaddata(final boolean cache) {

        final boolean mCache =cache;
        listOfPeople.clear();


        currentuser = User.getUsername();
        currentuseremail = User.getString(ParseTables.Users.EMAIL);
        currentuserinstituition = User.getString(ParseTables.Users.INSTITUTE);
        currentusername = User.getString(ParseTables.Users.NAME);
        currentuserqualification = User.getString(ParseTables.Users.QUALIFICATIONS);
        userlocation = User.getParseGeoPoint(ParseTables.Users.LOCATION);

//          ArrayList<ParseObject> interests = (ArrayList<ParseObject>) User.get(ParseTables.Users.INTERESTS);
//         ^^ can't fetch array list in back ground, therefore using a query with .include(ParseTables.Users.INTERESTS)


        ParseQuery<ParseUser> currentuserInterestsQuery = ParseUser.getQuery();
        if (cache)
            currentuserInterestsQuery.fromLocalDatastore();
        currentuserInterestsQuery.whereEqualTo("username", currentuser);
        currentuserInterestsQuery.include(ParseTables.Users.INTERESTS);
        currentuserInterestsQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(final ParseUser user, ParseException e) {
                if (user == null) {
                    if(ApplicationWrapper.LOG_DEBUG) Log.d("query", "failed.");
                } else {

                    final ArrayList<ParseObject> currentUserInterestsList = (ArrayList<ParseObject>) User.get(ParseTables.Users.INTERESTS);
                    if(currentuserinterests==null)

                    {
                        currentuserinterests = "";
                    }

                    if (!cache) {
                        ParseObject.unpinAllInBackground(ParseTables.People.PEOPLE_USER_INTERESTS, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                ParseObject.pinAllInBackground(ParseTables.People.PEOPLE_USER_INTERESTS, currentUserInterestsList);
                                doneFetchingUserInterests(currentUserInterestsList, mCache);
                            }
                        });
                    } else
                        doneFetchingUserInterests(currentUserInterestsList, mCache);



                      // The query was successful.
                }
            }
        });

    }



    public void doneFetchingUserInterests(ArrayList<ParseObject> currentUserInterestsList, final boolean cache) {

    if(!currentUserInterestsList.isEmpty())

    {
        for (int c = 0; c < currentUserInterestsList.size(); c++) {
            if (!currentUserInterestsList.get(c).equals("") || !(currentUserInterestsList.get(c) == null)) {


                ParseQuery<ParseUser> query = ParseUser.getQuery();
                if (cache)
                    query.fromLocalDatastore();
                query.include(ParseTables.Users.INTERESTS);
                query.whereEqualTo(ParseTables.Users.INTERESTS, currentUserInterestsList.get(c));

                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(final List<ParseUser> objects, ParseException e) {
                        if (e == null) {


                            if (!cache) {
                                ParseObject.unpinAllInBackground(ParseTables.People.PEOPLE_SAME_INTERESTS, new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        ParseObject.pinAllInBackground(ParseTables.People.PEOPLE_SAME_INTERESTS, objects);
                                        doneFetchingPeople(objects, cache);
                                    }
                                });
                            } else
                                doneFetchingPeople(objects, cache);
                        }

                        else {
                            // Something went wrong.
                        }

                    }
                });

            }
        }
    }

    else

    {
        progressBar.setVisibility(View.GONE);
    }

}



    public void doneFetchingPeople(List<ParseUser> objects, boolean cache) {

        HashMap<String, Boolean> existingelement = new HashMap<String, Boolean>();

        existingelement.clear();

        for (ParseUser pu : objects) {
            //access the data associated with the ParseUser using the get method
            //pu.getString("key") or pu.get("key")

            if (!pu.getUsername().equals(currentuser) && pu.getBoolean(ParseTables.Users.FULLY_REGISTERED)) {
                if (!existingelement.containsKey(pu.getUsername())) {

                    each = new EachRow3();
                    each.cname = pu.getString(ParseTables.Users.NAME);


                    ArrayList<ParseObject> personInterests = (ArrayList<ParseObject>) pu.get(ParseTables.Users.INTERESTS);

                    if(personInterests!=null && !personInterests.isEmpty()) {
                        StringBuilder stringBuilder = new StringBuilder("");
                        for (ParseObject parseObject : personInterests) {
                            try {
                                stringBuilder.append(parseObject.fetchIfNeeded().getString("name")).append(", ");
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                        stringBuilder.setLength(stringBuilder.length() - 2);
                        each.cinterests = stringBuilder.toString();

                    }


                    each.cqualification = pu.getString(ParseTables.Users.QUALIFICATIONS);
                    each.cinstituition = pu.getString(ParseTables.Users.INSTITUTE);
                    // each.cdistance = pu.getString(ParseTables.Users.NAME);
                    each.cusername = pu.getString(ParseTables.Users.USERNAME);
                    each.cauthData = pu.getString(ParseTables.Users.AUTHORIZATION);

                    ParseGeoPoint temploc = pu.getParseGeoPoint(ParseTables.Users.LOCATION);
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
                        each.fileObject = (ParseFile) pu.get(ParseTables.Users.IMAGE);

                    } catch (Exception e1) {
                        System.out.print("nahh");
                    }


                    listOfPeople.add(each);
                    existingelement.put(pu.getUsername(), true);
                }
            }
        }

        // The query was successful.

        q = new MyAdapter3(getActivity(), 0, listOfPeople);
        q.notifyDataSetChanged();

        lv.setAdapter(q);
        progressBar.setVisibility(View.GONE);
        lv.setVisibility(View.VISIBLE);
    }


    private void loaddataAfterSearch(final String textSearch, final boolean cache) {

        final boolean mCache =cache;
        listOfPeople.clear();
        q = new MyAdapter3(getActivity(), 0, listOfPeople);
        q.notifyDataSetChanged();
        lv.setAdapter(q);

        currentuser = User.getUsername();
        currentuseremail = User.getString(ParseTables.Users.EMAIL);
        currentuserinstituition = User.getString(ParseTables.Users.INSTITUTE);
        currentusername = User.getString(ParseTables.Users.NAME);
        currentuserqualification = User.getString(ParseTables.Users.QUALIFICATIONS);
        userlocation = User.getParseGeoPoint(ParseTables.Users.LOCATION);

//          ArrayList<ParseObject> interests = (ArrayList<ParseObject>) User.get(ParseTables.Users.INTERESTS);
//         ^^ can't fetch array list in back ground, therefore using a query with .include(ParseTables.Users.INTERESTS)


        ParseQuery<ParseUser> currentuserInterestsQuery = ParseUser.getQuery();
        if (cache)
            currentuserInterestsQuery.fromLocalDatastore();
        currentuserInterestsQuery.whereEqualTo("username", currentuser);
        currentuserInterestsQuery.include(ParseTables.Users.INTERESTS);
        currentuserInterestsQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(final ParseUser user, ParseException e) {
                if (user == null) {
                    if(ApplicationWrapper.LOG_DEBUG) Log.d("query", "failed.");
                } else {

                    final ArrayList<ParseObject> currentUserInterestsList = (ArrayList<ParseObject>) User.get(ParseTables.Users.INTERESTS);
                    if(currentuserinterests==null)

                    {
                        currentuserinterests = "";
                    }

                    if (!cache) {
                        ParseObject.unpinAllInBackground(ParseTables.People.PEOPLE_USER_INTERESTS, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                ParseObject.pinAllInBackground(ParseTables.People.PEOPLE_USER_INTERESTS, currentUserInterestsList);
                                doneFetchingUserInterestsForSearch(currentUserInterestsList, mCache, textSearch);
                            }
                        });
                    } else
                        doneFetchingUserInterestsForSearch(currentUserInterestsList, mCache,textSearch);



                    // The query was successful.
                }
            }
        });


    }

    public void doneFetchingUserInterestsForSearch(ArrayList<ParseObject> currentUserInterestsList, final boolean cache, String textSearch) {

        if(!currentUserInterestsList.isEmpty())

        {
            for (int c = 0; c < currentUserInterestsList.size(); c++) {
                if (!currentUserInterestsList.get(c).equals("") || !(currentUserInterestsList.get(c) == null)) {


                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    if (cache)
                        query.fromLocalDatastore();
                    query.whereEqualTo(ParseTables.Users.INTERESTS, currentUserInterestsList.get(c));
                    query.whereMatches(ParseTables.Users.NAME, "(" + textSearch + ")", "i");
                    query.include(ParseTables.Users.INTERESTS);


                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(final List<ParseUser> objects, ParseException e) {
                            if (e == null) {


                                if (!cache) {
                                    ParseObject.unpinAllInBackground(ParseTables.People.PEOPLE_SAME_INTERESTS, new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            ParseObject.pinAllInBackground(ParseTables.People.PEOPLE_SAME_INTERESTS, objects);
                                            doneFetchingPeople(objects, cache);
                                        }
                                    });
                                } else
                                    doneFetchingPeople(objects, cache);
                            } else {
                                // Something went wrong.
                            }

                        }
                    });

                }
            }
        }

        else

        {
            progressBar.setVisibility(View.GONE);
        }


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


//            Toast.makeText(getActivity(), row.cusername, Toast.LENGTH_SHORT).show();

                        if (row.fileObject != null) {
                            row.fileObject
                                    .getDataInBackground(new GetDataCallback() {
                                        public void done(byte[] data,
                                                         ParseException e) {
                                            if (e == null) {
                                                if(ApplicationWrapper.LOG_DEBUG) Log.d("test",
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
                            holder.userimg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_picture_blank_portrait));
                        }

                        return convertView;
                    }

                    @Override
                    public EachRow3 getItem(int position) {
                        // TODO Auto-generated method stub
                        return listOfPeople.get(position);
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
                    String cauthData;

                    Bitmap cbmp;
                    ParseFile fileObject;
                }
            }
