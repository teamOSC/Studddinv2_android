package in.tosc.studddin.fragments.listings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.ui.ParseCircularImageView;
import in.tosc.studddin.ui.ProgressBarCircular;

public class MyListingsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBarCircular loader;
    private View rootView;
    private ScrollView mEmptyView;
    private LinearLayout mListings;

    public MyListingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_listings, container, false);
        loader = (ProgressBarCircular) rootView.findViewById(R.id.progressBar);
        loader.setBackgroundColor(getResources().getColor(R.color.listingsColorPrimaryDark));
        mEmptyView = (ScrollView) rootView.findViewById(R.id.listing_empty);
        mListings = (LinearLayout) rootView.findViewById(R.id.my_listing);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listing_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        fetchMyListings();

        return rootView;
    }

    private void fetchMyListings() {
        ParseQuery<ParseObject> query = new ParseQuery<>(
                ParseTables.Listings.LISTINGS);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.whereEqualTo(ParseTables.Listings.OWNER_NAME, ParseUser.getCurrentUser().getString(ParseTables.Users.NAME));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    doneFetching(parseObjects);
                } else {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void doneFetching(List<ParseObject> parseObjects) {
        mAdapter = new MyListingAdapter(parseObjects);
        mAdapter.notifyDataSetChanged();
        loader.setVisibility(View.GONE);
        mRecyclerView.setAdapter(mAdapter);
        if (mAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mListings.setVisibility(View.GONE);
        }

    }

    public class MyListingAdapter extends RecyclerView.Adapter<MyListingAdapter.ViewHolder> {
        private List<ParseObject> mDataset;

        public MyListingAdapter(List<ParseObject> dataSet) {
            mDataset = dataSet;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            CardView v = (CardView) LayoutInflater.from(viewGroup.getContext())
                    .inflate(in.tosc.studddin.R.layout.listing_my_card_view, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            viewHolder.listing_name.setText(mDataset.get(i).getString(ParseTables.Listings.LISTING_NAME));
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yy");
            viewHolder.createdAt.setText(sdf.format(mDataset.get(i).getCreatedAt()));
            viewHolder.listing_desc.setText(mDataset.get(i).getString(ParseTables.Listings.LISTING_DESC));
            viewHolder.listing_image.setParseFile(mDataset.get(i).getParseFile(ParseTables.Listings.IMAGE));
            viewHolder.listing_image.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView listing_name;
            TextView createdAt;
            TextView listing_desc;
            ParseImageView listing_image;
            ImageView delete;

            public ViewHolder(CardView v) {
                super(v);
                this.listing_name = (TextView) v.findViewById(R.id.listing_name);
                this.delete = (ImageView) v.findViewById(R.id.listing_delete);
                this.createdAt = (TextView) v.findViewById(R.id.listing_created_at);
                this.listing_image = (ParseCircularImageView) v.findViewById(R.id.listing_image);
                this.listing_desc = (TextView) v.findViewById(R.id.listing_desc);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ParseObject object = mDataset.get(getPosition());
                        object.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    mDataset.remove(getPosition());
                                    notifyItemRemoved(getPosition());
                                    notifyItemRangeChanged(getPosition(), mDataset.size());
                                    fetchMyListings();
                                } else
                                    Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        }
    }


}
