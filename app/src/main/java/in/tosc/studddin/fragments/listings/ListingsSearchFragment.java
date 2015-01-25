package in.tosc.studddin.fragments.listings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

                return true;
            case R.id.listing_upload:
                Fragment fragment2 = new ListingsUploadFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.container, fragment2);
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


}
