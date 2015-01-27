package in.tosc.studddin.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.customview.MaterialEditText;
import in.tosc.studddin.fragments.signon.SignupDataFragment.UserDataFields;
import in.tosc.studddin.utils.HttpExecute;
import in.tosc.studddin.utils.HttpExecutor;

/**
 * News Feed fragment subclass
 */
public class FeedFragment extends Fragment implements View.OnKeyListener{

    private FeedRootAdapter mAdapter;
    View rootView;
    private RecyclerView recyclerView;

    private static Context context;

    private static final String TAG = "[[[OMERJERK]]]";

    private MaterialEditText searchEditText;

    private static final String KEY_LINK = "url";
    private static final String KEY_TITLE = "title";
    private static final String KEY_IMAGE_URL = "image";
    private static final String FEED_TABLE = "Feed";
    private static final String EVENTS_TABLE = "Events";
    private static final String KEY_LOCAL_DATASTORE = "feed";

    public static final int CATEGORY_INTERESTS = 0;
    public static final int CATEGORY_AROUND = 1;
    public static final int CATEGORY_COLLEGE = 2;

    String searchUrl = "tosc.in:8082/search?q=";

    String interests = "Physics, biology, Economics";
    List<String> interestList;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        searchEditText = (MaterialEditText) rootView.findViewById(R.id.feed_search);
        searchEditText.setOnKeyListener(this);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.feed_recycler_view);

        context = getActivity();

        // use a linear layout manager
        RecyclerView.LayoutManager mVerticalLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(mVerticalLayoutManager);

        mAdapter = new FeedRootAdapter();
        recyclerView.setAdapter(mAdapter);

        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.d(TAG, "interests = " + currentUser.getString(UserDataFields.USER_INTERESTS));

        interests = interests.replaceAll("\\s+", "");
        interests = interests.replaceAll(",", " ");
        String[] temp = interests.split(" ");
        interestList = new ArrayList();
        for (String list : temp) {
            Log.d(TAG, "LIST = " + list);
            interestList.add(list);
        }

        updateUI(CATEGORY_INTERESTS, FEED_TABLE, 0);
        updateUI(CATEGORY_COLLEGE, EVENTS_TABLE, 0);
        updateUI(CATEGORY_AROUND, FEED_TABLE, 0);
        getFeed();

        return rootView;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (v.getId() == R.id.feed_search) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        Toast.makeText(context, "Search", Toast.LENGTH_SHORT).show();
                        doSearch(((MaterialEditText) v).getText().toString());
                        return true;
                    default:
                        break;
                }
            }
        }
        return false;
    }

    private static class FeedRootAdapter extends RecyclerView.Adapter<FeedRootAdapter.ViewHolder> {
        private static final int CATEGORY_COUNT = 3;

        //Adapters for each category
        private FeedCategoryAdapter[] adapters = new FeedCategoryAdapter[CATEGORY_COUNT];
        private FeedRootWrapper[] mDataset = new FeedRootWrapper[CATEGORY_COUNT];

        public FeedRootAdapter() {
            for (int i = 0; i < CATEGORY_COUNT; ++i) {
                adapters[i] = new FeedCategoryAdapter();
                mDataset[i] = new FeedRootWrapper();
            }
        }

        public void setDataSet(int i, List<ParseObject> parseObjects, boolean isLoaded, String categoryName) {
            mDataset[i].setData(parseObjects, isLoaded, categoryName);
        }

        public void invalidateData(int i) {
            adapters[i].notifyDataSetChanged();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public CardView mCardView;
            public TextView mTextView;
            public RecyclerView mHorizontalRecyclerView;
            public ProgressBar progressBar;
            public ViewHolder(CardView v) {
                super(v);
                mCardView = v;
                mTextView = (TextView) mCardView.findViewById(R.id.feed_category_text);
                mHorizontalRecyclerView = (RecyclerView)
                        mCardView.findViewById(R.id.feed_category_horizontal_recycler_view);
                RecyclerView.LayoutManager mHorizontalLayoutManager = new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false);
                mHorizontalRecyclerView.setLayoutManager(mHorizontalLayoutManager);
                progressBar = (ProgressBar) mCardView.findViewById(R.id.feed_category_progress_bar);
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public FeedRootAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_root_list_card_view, parent, false);
            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mDataset[position].string);

            if (mDataset[position].isLoaded) {
                holder.progressBar.setVisibility(View.GONE);
                holder.mHorizontalRecyclerView.setVisibility(View.VISIBLE);
                FeedCategoryAdapter mFeedCategoryAdapter = new FeedCategoryAdapter();
                mFeedCategoryAdapter.setDataset(mDataset[position].parseObjects);
                holder.mHorizontalRecyclerView.setAdapter(mFeedCategoryAdapter);
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }

    private static class FeedCategoryAdapter extends RecyclerView.Adapter<FeedCategoryAdapter.FeedCategoryViewHolder> {

        List<ParseObject> parseObjects = new ArrayList();

        public void setDataset(List<ParseObject> parseObjects) {
            this.parseObjects = parseObjects;
        }

        public static class FeedCategoryViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public LinearLayout view;
            public TextView mTextView;
            public ImageView mImageView;
            public FeedCategoryViewHolder(LinearLayout v) {
                super(v);
                this.view = v;
                mTextView = (TextView) this.view.findViewById(R.id.feed_item_text_view);
                mImageView = (ImageView) this.view.findViewById(R.id.feed_item_image);
            }
        }

        @Override
        public FeedCategoryAdapter.FeedCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_category_item, parent, false);
            //FeedCategoryAdapter.FeedCategoryViewHolder vh = new FeedCategoryAdapter.FeedCategoryViewHolder(view);
            return new FeedCategoryAdapter.FeedCategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FeedCategoryViewHolder holder, int position) {

            final ParseObject object = parseObjects.get(position);
            holder.mTextView.setText(object.getString(KEY_TITLE));
            Ion.with(holder.mImageView)
//                    .placeholder(R.drawable.placeholder_image)
//                    .error(R.drawable.error_image)
//                    .animateLoad(spinAnimation)
//                    .animateIn(fadeInAnimation)
                    .load(object.getString(KEY_IMAGE_URL));
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(object.getString(KEY_LINK)));
                    context.startActivity(browserIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (parseObjects == null)
                return 0;
            return parseObjects.size();
        }
    }

    private static class FeedRootWrapper {
        public String string;
        public List<ParseObject> parseObjects;
        public boolean isLoaded = false;

        public void setData(List<ParseObject> parseObjects, boolean isLoaded, String categoryTitle) {
            this.parseObjects = parseObjects;
            this.string = categoryTitle;
            this.isLoaded = isLoaded;
        }

        public void setData(List<ParseObject> parseObjects) {
            setData(parseObjects, isLoaded, string);
        }
    }

    private int getCategoryResource(int i) throws UnsupportedOperationException{
        switch (i) {
            case 0:
                return R.string.feed_category_interests;
            case 1:
                return R.string.feed_category_around;
            case 2:
                return R.string.feed_category_college;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public void doSearch(String query) {
        String url = searchUrl + query;
        new HttpExecute(new HttpExecutor() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response = " + response);
            }
        }, url).execute();
    }

    public void getFeed() {

        /* Get data related to interest*/
        ParseQuery<ParseObject> interestQuery = ParseQuery.getQuery(FEED_TABLE).setLimit(10);
        interestQuery.whereContainedIn("category", interestList);
        interestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground(KEY_LOCAL_DATASTORE);
                    ParseObject.pinAllInBackground(KEY_LOCAL_DATASTORE, parseObjects);
                    updateUI(CATEGORY_INTERESTS, FEED_TABLE, 1);
                } else {
                    Log.e(TAG, "Getting feed query broke");
                }
            }
        });

        /*Get data related to user's college*/
        ParseQuery<ParseObject> collegeQuery = ParseQuery.getQuery(EVENTS_TABLE).setLimit(10);
        collegeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground(KEY_LOCAL_DATASTORE);
                    ParseObject.pinAllInBackground(parseObjects);
                    Log.d(TAG, "Calling update UI from getFeed College");
                    Log.d(TAG, "Random = " + parseObjects.get(0).getString(KEY_TITLE));
                    updateUI(CATEGORY_COLLEGE, EVENTS_TABLE, 1);
                } else {
                    Log.e(TAG, "Getting feed query broke");
                }
            }
        });

        /*Get data related to the around the user*/
        ParseQuery<ParseObject> aroundQuery = ParseQuery.getQuery(FEED_TABLE).setLimit(10);
        aroundQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground(KEY_LOCAL_DATASTORE);
                    ParseObject.pinAllInBackground(parseObjects);
                    Log.d(TAG, "Calling update UI from getFeed Around");
                    updateUI(CATEGORY_AROUND, FEED_TABLE, 1);
                } else {
                    Log.e(TAG, "Getting feed query broke");
                }
            }
        });
    }

    private void updateUI (final int i, String tableName, final int flag) {
        if (this.isAdded()) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName).fromLocalDatastore().setLimit(10);
//            query.whereContainedIn("category", interestList);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {

                        int resourceId = 0;
                        try {
                            resourceId = getCategoryResource(i);
                        } catch (UnsupportedOperationException ex) {
                            Toast.makeText(getActivity(), "Unsupported Operation", Toast.LENGTH_SHORT).show();
                        }
                        mAdapter.setDataSet(i, parseObjects, true, getString(resourceId));

                        mAdapter.invalidateData(i);
//                        if (flag == 1)
                            mAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Query failed");
                    }
                }
            });
        }
    }
}
