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

import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.utils.ProgressBarCircular;
import in.tosc.studddin.utils.Utilities;

public class MyListingsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBarCircular loader;
    private View rootView;

    public MyListingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_my_listings, container, false);
        loader = (ProgressBarCircular) rootView.findViewById(R.id.progressBar);
        loader.setBackgroundColor(getResources().getColor(R.color.pink));
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listing_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (Utilities.isNetworkAvailable(getActivity()))
            fetchMyListings(false);
        else
            fetchMyListings(true);

        return rootView;
    }

    private void fetchMyListings(final boolean cache){
        ParseQuery<ParseObject> query = new ParseQuery<>(
                "Listings");
        if (cache)
            query.fromLocalDatastore();
        query.whereEqualTo("ownerName", ParseUser.getCurrentUser().getString("NAME"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    if (!cache) {
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
                    Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void doneFetching(List<ParseObject> parseObjects, boolean cache) {
        mAdapter = new MyListingAdapter(parseObjects, cache);
        mAdapter.notifyDataSetChanged();
        loader.setVisibility(View.GONE);
        mRecyclerView.setAdapter(mAdapter);
    }

    public class MyListingAdapter extends RecyclerView.Adapter<MyListingAdapter.ViewHolder> {
        private List<ParseObject> mDataset;
        private boolean mCache;

        public MyListingAdapter(List<ParseObject> dataSet, boolean cache) {
            mDataset = dataSet;
            mCache = cache;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            CardView v = (CardView) LayoutInflater.from(viewGroup.getContext())
                    .inflate(in.tosc.studddin.R.layout.listing_my_card_view, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            viewHolder.listing_name.setText(mDataset.get(i).getString("listingName"));
            viewHolder.createdAt.setText(mDataset.get(i).getCreatedAt().toString());
            viewHolder.listing_desc.setText(mDataset.get(i).getString("listingDesc"));
            if (!mCache)
                viewHolder.listing_image.setPlaceholder(getResources().getDrawable(R.drawable.listing_placeholder));
            viewHolder.listing_image.setParseFile(mDataset.get(i).getParseFile("image"));
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
                this.listing_image = (ParseImageView) v.findViewById(R.id.listing_image);
                this.listing_desc = (TextView) v.findViewById(R.id.listing_desc);
            }
        }
    }


}
