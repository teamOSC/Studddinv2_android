package in.tosc.studddin.fragments.people;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import in.tosc.studddin.ApplicationWrapper;
import in.tosc.studddin.R;
import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.ui.ProgressBarCircular;

public class PeopleNearmeFragment extends PeopleListFragment {

    Dialog dialogPeople;

    ParseGeoPoint userlocation = new ParseGeoPoint(0, 0);

    ArrayList<ParseObject> interests = new ArrayList<ParseObject>();

    EditText search;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people_nearme, container, false);
        progressBar = (ProgressBarCircular) view.findViewById(R.id.progressbar_people);
        progressBar.setBackgroundColor(getResources().getColor(R.color.peopleColorPrimaryDark));
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
                // ALWAYS SEARCH FROM CACHE
                loaddataAfterSearch(editable.toString());
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
                                        if (ApplicationWrapper.LOG_DEBUG) Log.d("test",
                                                "We've got data in data.");

                                        in.putByteArray("pic", data);
                                        System.out.print("pic3" + String.valueOf(data));
                                        transaction.add(R.id.container, newFragment).hide(PeopleNearmeFragment.this).addToBackStack(PeopleNearmeFragment.class.getName()).commit();


                                    } else {
                                        if (ApplicationWrapper.LOG_DEBUG)
                                            Log.d("test", "There was a problem downloading the data.");
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

                    transaction.add(R.id.container, newFragment).hide(PeopleNearmeFragment.this).addToBackStack(PeopleNearmeFragment.class.getName()).commit();

                }

            }
        });

        return view;
    }

    private void loaddata() {

        listOfPeople.clear();

        currentuser = ParseUser.getCurrentUser().getUsername();
        currentuseremail = ParseUser.getCurrentUser().getString(ParseTables.Users.EMAIL);
        if(ParseUser.getCurrentUser().get(ParseTables.Users.INSTITUTE)!=null)
        {
//            currentuserinstituition = ParseUser.getCurrentUser().getParseObject(ParseTables.Users.INSTITUTE).getString(ParseTables.College.NAME);
        }
        else{
            currentuserinstituition = " - " ;
        }        currentusername = ParseUser.getCurrentUser().getString(ParseTables.Users.NAME);
        currentuserqualification = ParseUser.getCurrentUser().getString(ParseTables.Users.QUALIFICATIONS);
        userlocation = ParseUser.getCurrentUser().getParseGeoPoint(ParseTables.Users.LOCATION);

        try {
            interests = (ArrayList<ParseObject>) ParseUser.getCurrentUser().get(ParseTables.Users.INTERESTS);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // DUMMY DATA SO THAT IT DISPLAYS SOMETHING
        if (userlocation == null || userlocation.getLatitude() == 0) {
            userlocation = new ParseGeoPoint(28.7434552,77.1205612);
        }

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.whereNear(ParseTables.Users.LOCATION, userlocation);
        query.include(ParseTables.Users.INTERESTS);
        query.include(ParseTables.Users.INSTITUTE);


        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(final List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    doneFetchingPeople(objects);
                    // The query was successful.
                } else {
                    // Something went wrong.
                }
            }
        });

    }

    public void doneFetchingPeople(List<ParseUser> objects) {

        if (objects == null) return;

        for (ParseUser pu : objects) {
            //access the data associated with the ParseUser using the get method
            //pu.getString("key") or pu.get("key")

            if (!pu.getUsername().equals(currentuser) && pu.getBoolean(ParseTables.Users.FULLY_REGISTERED)) {

                each = new EachRow3();
                each.cname = pu.getString(ParseTables.Users.NAME);

                ArrayList<ParseObject> personInterests = (ArrayList<ParseObject>) pu.get(ParseTables.Users.INTERESTS);

                if (personInterests != null && !personInterests.isEmpty()) {
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
                if(pu.getParseObject(ParseTables.Users.INSTITUTE)!=null)
                {
                    each.cinstituition = pu.getParseObject(ParseTables.Users.INSTITUTE).getString(ParseTables.College.NAME);
                }
                else{
                    each.cinstituition = " - " ;
                }

//                                          each.cdistance = pu.getString(ParseTables.Users.NAME);
                each.cusername = pu.getString(ParseTables.Users.USERNAME);
                JSONObject s = pu.getJSONObject(ParseTables.Users.AUTHORIZATION);
                if (s == null) {
                    each.cauthData = " - ";
                } else {
                    each.cauthData = s.toString();
                }

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
                    each.fileObject = (ParseFile) pu.getParseFile(ParseTables.Users.IMAGE);
                } catch (Exception e1) {
                    System.out.print("nahh");
                }

                listOfPeople.add(each);


            }
        }


        q = new MyAdapter3(getActivity(), 0, listOfPeople);
        q.notifyDataSetChanged();

        lv.setAdapter(q);
        progressBar.setVisibility(View.GONE);
        lv.setVisibility(View.VISIBLE);

    }


    private void loaddataAfterSearch(String textSearch) {

        listOfPeople.clear();
        q = new MyAdapter3(getActivity(), 0, listOfPeople);
        q.notifyDataSetChanged();

        lv.setAdapter(q);

        currentuser = ParseUser.getCurrentUser().getUsername();
        currentuseremail = ParseUser.getCurrentUser().getString(ParseTables.Users.EMAIL);
        if(ParseUser.getCurrentUser().get(ParseTables.Users.INSTITUTE)!=null)
        {
            currentuserinstituition = ParseUser.getCurrentUser().getParseObject(ParseTables.Users.INSTITUTE).getString(ParseTables.College.NAME);
        }
        else{
            currentuserinstituition = " - " ;
        }
        currentusername = ParseUser.getCurrentUser().getString(ParseTables.Users.NAME);
        currentuserqualification = ParseUser.getCurrentUser().getString(ParseTables.Users.QUALIFICATIONS);
        userlocation = ParseUser.getCurrentUser().getParseGeoPoint(ParseTables.Users.LOCATION);

        try {
            interests = (ArrayList<ParseObject>) ParseUser.getCurrentUser().get(ParseTables.Users.INTERESTS);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        // DUMMY DATA SO THAT IT DISPLAYS SOMETHING
        if (userlocation == null || userlocation.getLatitude() == 0) {
            userlocation = new ParseGeoPoint(28.7434552, 77.1205612);
        }

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.whereNear(ParseTables.Users.LOCATION, userlocation);
        query.whereMatches(ParseTables.Users.NAME, "(" + textSearch + ")", "i");
        query.include(ParseTables.Users.INTERESTS);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(final List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    doneFetchingPeople(objects);
                    // The query was successful.
                } else {
                    // Something went wrong.
                }
            }
        });

    }


}
