package in.tosc.studddin.fragments.listings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.internal.widget.TintCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.fragments.ListingsFragment;
import in.tosc.studddin.utils.ProgressBarCircular;
import in.tosc.studddin.utils.Utilities;

public class ListingsSearchFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBarCircular loader;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private boolean onRefresh = false;

    private SharedPreferences filterPrefs;
    private SharedPreferences.Editor editor;

    private ParseGeoPoint location;

    public ListingsSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        filterPrefs = getActivity().getSharedPreferences("filterdetails", 0);
        editor = filterPrefs.edit();
        editor.putBoolean("books", true);
        editor.putBoolean("apparatus", true);
        editor.putBoolean("misc", true);
        editor.putString("sortby", "nearest");
        editor.commit();

        location = ParseUser.getCurrentUser().getParseGeoPoint("location");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listings_search, container, false);
        loader = (ProgressBarCircular) rootView.findViewById(R.id.progressBar);
        loader.setBackgroundColor(getResources().getColor(R.color.pink));
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listing_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        EditText search = (EditText) rootView.findViewById(R.id.listing_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                fetchListings(true, editable.toString());
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(getActivity())) {
                    onRefresh = true;
                    fetchListings(false, null);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Utilities.isNetworkAvailable(getActivity()))
            fetchListings(false, null);
        else
            fetchListings(true, null);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.listings_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.listing_filter:
                FilterDialog dialog = new FilterDialog();
                dialog.show(getFragmentManager(), "filterdialog");
                return true;
            case R.id.listing_upload:
                ListingsFragment lFragment = (ListingsFragment) getParentFragment();
                lFragment.goToOtherFragment(1);
                return true;
            case R.id.listing_my:
                ListingsFragment fragment = (ListingsFragment) getParentFragment();
                fragment.goToOtherFragment(2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchListings(final boolean cache, String text) {
        final ArrayList<String> categories = new ArrayList<>();
        if (filterPrefs.getBoolean("books", true))
            categories.add("Book");
        if (filterPrefs.getBoolean("apparatus", true))
            categories.add("Apparatus");
        if (filterPrefs.getBoolean("misc", true))
            categories.add("Misc.");
        ParseQuery<ParseObject> query = new ParseQuery<>(
                "Listings");
        if (cache)
            query.fromLocalDatastore();
        query.whereContainedIn(ParseTables.Listings.CATEGORY, categories);
        if (text == null) {
            if (filterPrefs.getString("sortby", "nearest").compareTo("recent") == 0)
                query.orderByDescending(ParseTables.Listings.CREATED_AT);
        /*else
            query.whereNear("location",new ParseGeoPoint(28.7500749,77.11766519999992));*/
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(final List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        if (categories.size() == 3 && !cache) {
                            ParseObject.unpinAllInBackground("listings", new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    ParseObject.pinAllInBackground("listings", parseObjects);
                                    doneFetching(parseObjects, cache);
                                }
                            });
                        } else
                            doneFetching(parseObjects, cache);
                    } else {
                        if (onRefresh) {
                            onRefresh = false;
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        loader.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (text != null) {
            query.whereMatches(ParseTables.Listings.LISTING_NAME, "(" + text + ")", "i");
            ParseQuery<ParseObject> descQuery = new ParseQuery<>(
                    "Listings");
            descQuery.fromLocalDatastore();
            descQuery.whereContainedIn(ParseTables.Listings.CATEGORY, categories);
            descQuery.whereMatches(ParseTables.Listings.LISTING_DESC, "(" + text + ")", "i");
            List<ParseQuery<ParseObject>> queries = new ArrayList<>();
            queries.add(query);
            queries.add(descQuery);
            ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
            if (filterPrefs.getString("sortby", "nearest").compareTo("recent") == 0)
                mainQuery.orderByDescending(ParseTables.Listings.CREATED_AT);
            /*else
                mainQuery.whereNear("location",new ParseGeoPoint(28.7500749,77.11766519999992));*/
            mainQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> results, ParseException e) {
                    doneFetching(results, cache);
                }
            });
        }
    }

    private void doneFetching(List<ParseObject> parseObjects, boolean cache) {
        mAdapter = new ListingAdapter(parseObjects, cache);
        mAdapter.notifyDataSetChanged();
        if (onRefresh) {
            onRefresh = false;
            swipeRefreshLayout.setRefreshing(false);
        } else
            loader.setVisibility(View.GONE);
        mRecyclerView.setAdapter(mAdapter);
    }

    public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {
        private List<ParseObject> mDataset;
        private boolean mCache;

        public ListingAdapter(List<ParseObject> dataSet, boolean cache) {
            mDataset = dataSet;
            mCache = cache;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            CardView v = (CardView) LayoutInflater.from(viewGroup.getContext())
                    .inflate(in.tosc.studddin.R.layout.listing_card_view, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            viewHolder.listing_name.setText(mDataset.get(i).getString(ParseTables.Listings.LISTING_NAME));
            viewHolder.owner_name.setText(mDataset.get(i).getString(ParseTables.Listings.OWNER_NAME));
            viewHolder.mobile.setText(mDataset.get(i).getString(ParseTables.Listings.MOBILE));
            viewHolder.listing_desc.setText(mDataset.get(i).getString(ParseTables.Listings.LISTING_DESC));
            if (!mCache)
                viewHolder.listing_image.setPlaceholder(getResources().getDrawable(R.drawable.listing_placeholder));
            viewHolder.listing_image.setParseFile(mDataset.get(i).getParseFile(ParseTables.Listings.IMAGE));
            viewHolder.listing_image.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                }
            });
            try{
                double distance = mDataset.get(i).getParseGeoPoint(ParseTables.Listings.LOCATION).distanceInKilometersTo(location);
                if (distance < 10 && distance > 0)
                    viewHolder.listing_distance.setText(String.format("%.1f", distance) + " km away");
                else
                    viewHolder.listing_distance.setText((int) distance + " km away");
                final String latitude = Double.toString(mDataset.get(i).getParseGeoPoint(ParseTables.Listings.LOCATION).getLatitude());
                final String longitude = Double.toString(mDataset.get(i).getParseGeoPoint(ParseTables.Listings.LOCATION).getLongitude());
                viewHolder.compass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("geo:0,0?q=" + latitude + "," + longitude + "(" + mDataset.get(i).getString(ParseTables.Listings.OWNER_NAME) + ")"));
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?q=loc:" + latitude + "," + longitude)));
                        }
                    }
                });
            }catch(Exception e){
                viewHolder.listing_distance.setText("Unknown");
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView listing_name;
            TextView owner_name;
            TextView mobile;
            TextView listing_desc;
            ParseImageView listing_image;
            TextView listing_distance;
            ImageView compass;

            public ViewHolder(CardView v) {
                super(v);
                this.listing_name = (TextView) v.findViewById(R.id.listing_name);
                this.compass = (ImageView) v.findViewById(R.id.compass);
                this.owner_name = (TextView) v.findViewById(R.id.owner_name);
                this.mobile = (TextView) v.findViewById(R.id.mobile);
                this.listing_distance = (TextView) v.findViewById(R.id.listing_distance);
                this.listing_image = (ParseImageView) v.findViewById(R.id.listing_image);
                this.listing_desc = (TextView) v.findViewById(R.id.listing_desc);
            }
        }
    }

    @SuppressLint("ValidFragment")
    public class FilterDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {
        private View v;
        private CheckBox books;
        private CheckBox apparatus;
        private CheckBox misc;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            filterPrefs = getActivity().getSharedPreferences("filterdetails", 0);
            editor = filterPrefs.edit();
            List<String> spinnerList = new ArrayList<>();
            spinnerList.add("Nearest");
            spinnerList.add("Recent");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerList);
            AlertDialog.Builder filterDialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            v = inflater.inflate(R.layout.listing_filter_dialog, null);
            books = (TintCheckBox) v.findViewById(R.id.cb_books);
            apparatus = (TintCheckBox) v.findViewById(R.id.cb_apparatus);
            misc = (TintCheckBox) v.findViewById(R.id.cb_misc);
            books.setChecked(filterPrefs.getBoolean("books", true));
            apparatus.setChecked(filterPrefs.getBoolean("apparatus", true));
            misc.setChecked(filterPrefs.getBoolean("misc", true));
            Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(this);
            if (filterPrefs.getString("sortby", "nearest").compareTo("nearest") == 0)
                spinner.setSelection(0);
            else
                spinner.setSelection(1);
            filterDialog.setView(v);
            filterDialog.setTitle("Filter");
            filterDialog.setCancelable(false);
            filterDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (books.isChecked())
                        editor.putBoolean("books", true);
                    else
                        editor.putBoolean("books", false);

                    if (apparatus.isChecked())
                        editor.putBoolean("apparatus", true);
                    else
                        editor.putBoolean("apparatus", false);

                    if (misc.isChecked())
                        editor.putBoolean("misc", true);
                    else
                        editor.putBoolean("misc", false);

                    editor.commit();
                    fetchListings(true, null);
                }
            });
            return filterDialog.create();
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (i == 0) {
                editor.putString("sortby", "nearest");
                editor.commit();
            } else if (i == 1) {
                editor.putString("sortby", "recent");
                editor.commit();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }
}