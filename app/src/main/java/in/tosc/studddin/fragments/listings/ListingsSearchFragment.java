package in.tosc.studddin.fragments.listings;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.internal.widget.TintCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import in.tosc.studddin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListingsSearchFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    View rootView;


    public ListingsSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences filterDetails = getActivity().getSharedPreferences("filterdetails", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_listings, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listing_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        ListingInfo[] listing = new ListingInfo[10];
        for (int i = 0; i < 10; ++i) {
            listing[i] = new ListingInfo();
        }

        mAdapter = new ListingAdapter(listing);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.listing, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.listing_filter:
                FilterDialog dialog = new FilterDialog();
                dialog.show(getFragmentManager(),"filterdialog");

                return true;
            case R.id.listing_upload:
                Fragment fragment2 = new ListingsUploadFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.notes_pager, fragment2);
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ListingInfo {

        protected String owner_name;
        protected String listing_name;
        protected String mobile;
        protected String distance;
        protected ImageView listing_image;


    }

    public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder>{


        private ListingInfo[] mDataset;

        public ListingAdapter(ListingInfo[] dataSet) {
            mDataset = dataSet;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            CardView v = (CardView) LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.listing_card_view, viewGroup, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return mDataset.length;
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            public CardView mCardView;
            public ViewHolder(CardView v) {
                super(v);
                mCardView = v;
            }
        }
    }

    public static class FilterDialog extends DialogFragment implements AdapterView.OnItemSelectedListener{

        private View v;
        private CheckBox books;
        private CheckBox apparatus;
        private CheckBox misc;
        SharedPreferences filterPrefs;
        SharedPreferences.Editor editor;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            filterPrefs = getActivity().getSharedPreferences("filterdetails", 0);
            editor = filterPrefs.edit();
            List<String> spinnerList = new ArrayList<String>();
            spinnerList.add("Nearest");
            spinnerList.add("Recent");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,spinnerList);
            AlertDialog.Builder filterDialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            v = inflater.inflate(R.layout.listing_filter_dialog, null);
            books = (TintCheckBox) v.findViewById(R.id.cb_books);
            apparatus = (TintCheckBox) v.findViewById(R.id.cb_apparatus);
            misc = (TintCheckBox) v.findViewById(R.id.cb_misc);
            books.setChecked(filterPrefs.getBoolean("books",true));
            apparatus.setChecked(filterPrefs.getBoolean("apparatus",true));
            misc.setChecked(filterPrefs.getBoolean("misc",true));
            Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(this);
            if(filterPrefs.getString("sortby","nearest").compareTo("nearest")==0)
                spinner.setSelection(0);
            else
                spinner.setSelection(1);
            filterDialog.setView(v);
            filterDialog.setTitle("Filter");
            filterDialog.setCancelable(false);
            filterDialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(books.isChecked())
                        editor.putBoolean("books",true);
                    else
                        editor.putBoolean("books",false);

                    if(apparatus.isChecked())
                        editor.putBoolean("apparatus",true);
                    else
                        editor.putBoolean("apparatus",false);

                    if(misc.isChecked())
                        editor.putBoolean("misc",true);
                    else
                        editor.putBoolean("books",false);

                    editor.commit();

                   //refresh listing
                }
            });
            return filterDialog.create();
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(i==0){
                editor.putString("sortby","nearest");
                editor.commit();
            }
            else if(i==1){
                editor.putString("sortby","recent");
                editor.commit();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }


    }



}
