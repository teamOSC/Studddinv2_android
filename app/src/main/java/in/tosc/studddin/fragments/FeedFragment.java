package in.tosc.studddin.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import in.tosc.studddin.ApplicationWrapper;
import in.tosc.studddin.R;
import in.tosc.studddin.customview.MaterialEditText;
import in.tosc.studddin.utils.ProgressBarCircular;
import in.tosc.studddin.utils.Utilities;

/**
 * News Feed fragment subclass
 */
public class FeedFragment extends Fragment implements View.OnKeyListener {

    public static final int CATEGORY_INTERESTS = 0;
    public static final int CATEGORY_AROUND = 1;
    public static final int CATEGORY_COLLEGE = 2;
    private static final String TAG = "FeedFragment";
    private static final String KEY_LINK = "url";
    private static final String KEY_TITLE = "title";
    private static final String KEY_IMAGE_URL = "image";
    private static final String FEED_TABLE = "Feed";
    private static final String EVENTS_TABLE = "Events";
    private static final String KEY_LOCAL_DATASTORE = "feed";
    private static Context context;
    View rootView;
    List<String> interestList = new ArrayList();
    private FeedRootAdapter mAdapter;
    private RecyclerView recyclerView;
    private MaterialEditText searchEditText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton searchButton;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setHasOptionsMenu(true);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        int p = getActivity().getResources().getColor(R.color.colorPrimary);
        int s = getActivity().getResources().getColor(R.color.colorPrimaryDark);
        ApplicationWrapper.setCustomTheme((ActionBarActivity) getActivity(), p, s);
        searchEditText = (MaterialEditText) rootView.findViewById(R.id.feed_search);
        searchEditText.setOnKeyListener(this);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.feed_recycler_view);
        searchButton = (ImageButton) rootView.findViewById(R.id.searchblahblah);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setFocusableInTouchMode(true);
                searchEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }
        });
        context = getActivity();
        RecyclerView.LayoutManager mVerticalLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(mVerticalLayoutManager);

        mAdapter = new FeedRootAdapter();
        recyclerView.setAdapter(mAdapter);
        
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayoutfeed);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(getActivity())) {
                    getFeed();
                    Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT).show();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Interests");
        query.whereEqualTo("users", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                for (ParseObject parseObject : parseObjects) {
                    interestList.add(parseObject.getString("name"));
                }
            }
        });

        return rootView;
    }

    private void updateAll() {
        updateUI(CATEGORY_INTERESTS, FEED_TABLE, 0);
        updateUI(CATEGORY_COLLEGE, EVENTS_TABLE, 0);
        updateUI(CATEGORY_AROUND, FEED_TABLE, 0);
        getFeed();
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_feed, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            //case R.id.action_feed_refresh:
                //getFeed();
                //Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT).show();
                //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //activity.setTheme(R.style.AppTheme_Custom);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (v.getId() == R.id.feed_search) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        doSearch(((MaterialEditText) v).getText().toString());
                        return true;
                    default:
                        break;
                }
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAll();
    }

    private int getCategoryResource(int i) throws UnsupportedOperationException {
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
        Uri uri = Uri.parse("http://www.google.com/#q=" + query);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        /*
        //TODO:: Use our own search engine
        String url = searchUrl + query;
        new HttpExecute(new HttpExecutor() {
            @Override
            public void onResponse(String response) {
                Log.w(TAG, "Response = " + response);
            }
        }, url).execute(); */
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
                    Log.w(TAG, "Calling update UI from getFeed College");
                    Log.w(TAG, "Random = " + parseObjects.get(0).getString(KEY_TITLE));
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
                    Log.w(TAG, "Calling update UI from getFeed Around");
                    updateUI(CATEGORY_AROUND, FEED_TABLE, 1);
                } else {
                    Log.e(TAG, "Getting feed query broke");
                }
            }
        });
    }

    private synchronized void updateUI(final int i, String tableName, final int flag) {
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
                            Log.e(TAG, "Unsupported Operation");
                        }
                        if (FeedFragment.this.isAdded()) {
                            mAdapter.setDataSet(i, parseObjects, true, getString(resourceId));
                            mAdapter.invalidateData(i);
                        }
                    } else {
                        Log.e(TAG, "Query failed");
                        e.printStackTrace();
                    }
                }
            });
        }
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
            notifyItemChanged(i);
        }

        @Override
        public FeedRootAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
            // create a new view
            CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_root_list_card_view, parent, false);
            return new ViewHolder(v);
        }

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

        @Override
        public int getItemCount() {
            return mDataset.length;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public CardView mCardView;
            public TextView mTextView;
            public RecyclerView mHorizontalRecyclerView;
            public ProgressBarCircular progressBar;

            public ViewHolder(CardView v) {
                super(v);
                mCardView = v;
                mTextView = (TextView) mCardView.findViewById(R.id.feed_category_text);
                mTextView.setVisibility(View.VISIBLE);
                mHorizontalRecyclerView = (RecyclerView)
                        mCardView.findViewById(R.id.feed_category_horizontal_recycler_view);
                RecyclerView.LayoutManager mHorizontalLayoutManager = new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false);
                mHorizontalRecyclerView.setLayoutManager(mHorizontalLayoutManager);
                progressBar = (ProgressBarCircular) mCardView.findViewById(R.id.feed_category_progress_bar);
                progressBar.setBackgroundColor(context.getResources().getColor(R.color.pink));
            }
        }
    }

    private static class FeedCategoryAdapter extends RecyclerView.Adapter<FeedCategoryAdapter.FeedCategoryViewHolder> {

        List<ParseObject> parseObjects = new ArrayList();

        public void setDataset(List<ParseObject> parseObjects) {
            this.parseObjects = parseObjects;
        }

        @Override
        public FeedCategoryAdapter.FeedCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_category_item, parent, false);
            return new FeedCategoryAdapter.FeedCategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FeedCategoryViewHolder holder, int position) {

            final ParseObject object = parseObjects.get(position);
            holder.mTextView.setText(object.getString(KEY_TITLE));
            Ion.with(holder.mImageView).load(object.getString(KEY_IMAGE_URL));
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uriString = object.getString(KEY_LINK);
                    if (uriString != null) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(uriString));
                        context.startActivity(browserIntent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (parseObjects == null)
                return 0;
            return parseObjects.size();
        }

        public static class FeedCategoryViewHolder extends RecyclerView.ViewHolder {
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

}
