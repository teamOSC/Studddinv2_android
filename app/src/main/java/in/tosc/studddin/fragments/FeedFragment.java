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
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.utils.HttpExecute;
import in.tosc.studddin.utils.HttpExecutor;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment implements View.OnKeyListener{

    private RecyclerView mRecyclerView;
    private FeedRootAdapter mAdapter;
    private static RecyclerView.LayoutManager mVerticalLayoutManager;
    View rootView;

    private static Context context;

    private static final String TAG = "[[[OMERJERK]]]";

    private EditText searchEditText;

    private static final String KEY_LINK = "url";
    private static final String KEY_TITLE = "title";
    private static final String KEY_IMAGE_URL = "image";
    private static final String FEED_TABLE = "Feed";
    private static final String KEY_LOCAL_DATASTORE = "feed";

    public static final int CATEGORY_INTERESTS = 0;
    public static final int CATEGORY_AROUND = 1;
    public static final int CATEGORY_COLLEGE = 2;

    private boolean isQueryRunning = false;

    String searchUrl = "tosc.in:8082/search?q=";

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
        searchEditText = (EditText) rootView.findViewById(R.id.feed_search);
        searchEditText.setOnKeyListener(this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.feed_recycler_view);

        context = getActivity();

        // use a linear layout manager
        mVerticalLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mVerticalLayoutManager);

        mAdapter = new FeedRootAdapter();
        mRecyclerView.setAdapter(mAdapter);

        updateUI(CATEGORY_INTERESTS, 0);
        updateUI(CATEGORY_COLLEGE, 0);
        updateUI(CATEGORY_AROUND, 0);
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
                        doSearch(((EditText) v).getText().toString());
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

        public void setDataSet(int i, List<ParseObject> parseObjects, String categoryName) {
            mDataset[i].setData(parseObjects, categoryName);
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
            public ViewHolder(CardView v) {
                super(v);
                mCardView = v;
                mTextView = (TextView) mCardView.findViewById(R.id.feed_category_text);
                mHorizontalRecyclerView = (RecyclerView)
                        mCardView.findViewById(R.id.feed_category_horizontal_recycler_view);
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public FeedRootAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_root_list_card_view, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mDataset[position].string);

            RecyclerView.LayoutManager mHorizontalLayoutManager = new LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false);

            FeedCategoryAdapter mFeedCategoryAdapter = new FeedCategoryAdapter();
            mFeedCategoryAdapter.setDataset(mDataset[position].parseObjects);
            holder.mHorizontalRecyclerView.setAdapter(mFeedCategoryAdapter);
            holder.mHorizontalRecyclerView.setLayoutManager(mHorizontalLayoutManager);
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

        public void setData(List<ParseObject> parseObjects, String categoryTitle) {
            this.parseObjects = parseObjects;
            this.string = categoryTitle;
        }

        public void setData(List<ParseObject> parseObjects) {
            setData(parseObjects, string);
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

    /*
    private class ReadFromJSON extends AsyncTask<Void, Void, Void> {

        private String json;

        public ReadFromJSON(String json) {
            this.json = json;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONArray mJsonArray = (JSONArray) jsonArray.get(0);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private String getJSON() {
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.feed);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            Log.d(TAG, "String = " + new String(b));
            return new String(b);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    } */

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
        interestQuery.whereEqualTo("category", "Economics");
        interestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground(KEY_LOCAL_DATASTORE);
                    ParseObject.pinAllInBackground(KEY_LOCAL_DATASTORE, parseObjects);
                    updateUI(CATEGORY_INTERESTS, 1);
                } else {
                    Log.e(TAG, "Getting feed query broke");
                }
            }
        });

        /*Get data related to user's college*/
        ParseQuery<ParseObject> collegeQuery = ParseQuery.getQuery(FEED_TABLE).setLimit(10);
        collegeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(parseObjects);
                    Log.d(TAG, "Calling update UI from getFeed College");
                    updateUI(CATEGORY_COLLEGE, 1);
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
                    ParseObject.pinAllInBackground(parseObjects);
                    Log.d(TAG, "Calling update UI from getFeed Around");
                    updateUI(CATEGORY_AROUND, 1);
                } else {
                    Log.e(TAG, "Getting feed query broke");
                }
            }
        });
    }

    private void updateUI (final int i, final int flag) {
        if (isAdded()) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(FEED_TABLE).fromLocalDatastore();
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        int resourceId = 0;
                        try {
                            resourceId = getCategoryResource(i);
                        } catch (UnsupportedOperationException ex) {
                            Toast.makeText(getActivity(), "Unsupported Operation", Toast.LENGTH_SHORT).show();
                        }
                        mAdapter.setDataSet(i, parseObjects, getString(resourceId));

                                mAdapter.invalidateData(i);
                                mAdapter.notifyDataSetChanged();

                    } else {
                        Log.e(TAG, "Query failed");
                    }
                }
            });
        }
    }
}
