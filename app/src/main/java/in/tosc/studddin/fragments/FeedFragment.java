package in.tosc.studddin.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.tosc.studddin.ApplicationWrapper;
import in.tosc.studddin.R;
import in.tosc.studddin.ui.MaterialEditText;
import in.tosc.studddin.utils.Utilities;

/**
 * News Feed fragment subclass
 */
public class FeedFragment extends Fragment implements View.OnKeyListener {
    public static final boolean DEBUG = ApplicationWrapper.LOG_DEBUG;
    public static final boolean INFO = ApplicationWrapper.LOG_INFO;

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
    public static final String feedTags[] = {"Interests","Around","College"};
    private static Context context;
    View rootView;
    List<String> interestList = new ArrayList();
    private FeedCategoryAdapter mAdapter;
    private RecyclerView recyclerView;
    private EditText searchEditText;
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
        searchEditText = (EditText) rootView.findViewById(R.id.feed_search);
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

        StaggeredGridLayoutManager mGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);


        recyclerView.setLayoutManager(mVerticalLayoutManager);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(mVerticalLayoutManager);
        }
        else {
            recyclerView.setLayoutManager(mGridLayoutManager);
        }
        
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayoutfeed);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(getActivity())) {
                    getFeed();
                }
                 else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Interests");
        query.whereEqualTo("users", currentUser);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
//                if(Utilities.isNetworkAvailable(getActivity())){
//                    ParseObject.unpinAllInBackground("feed_interests", new DeleteCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            Log.d(TAG,"pinning");
//                            ParseObject.pinAllInBackground("feed_interests", parseObjects);
//                        }
//                    });
//                }
                for (ParseObject parseObject : parseObjects) {
                    interestList.add(parseObject.getString("name"));
                }
                getFeed();
            }
        });

        return rootView;
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

        final List<ParseObject> feedList = new ArrayList<>();

        ParseQuery<ParseObject> interestQuery = ParseQuery.getQuery(FEED_TABLE);
        interestQuery.whereContainedIn("category", interestList);
        interestQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        interestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    feedList.addAll(parseObjects);
//                    if(internet){
//                        ParseObject.unpinAllInBackground("feed_interestQuery", new DeleteCallback() {
//                            @Override
//                            public void done(ParseException e) {
//                                Log.d(TAG,"pinning");
//                                ParseObject.pinAllInBackground("feed_interestQuery", parseObjects);
//                            }
//                        });
//                    }
                    Collections.shuffle(feedList);
                    if(mAdapter == null){
                        mAdapter = new FeedCategoryAdapter(feedList);
                        recyclerView.setAdapter(mAdapter);
                    }
                    else{
                        mAdapter.notifyDataSetChanged();
                    }

                } else {
                    if(swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Log.e(TAG, "Getting feed query broke");
                }
            }
        });

        ParseQuery<ParseObject> collegeQuery = ParseQuery.getQuery(EVENTS_TABLE);
        collegeQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        collegeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    feedList.addAll(parseObjects);
//                    if(internet){
//                        ParseObject.unpinAllInBackground("feed_collegeQuery", new DeleteCallback() {
//                            @Override
//                            public void done(ParseException e) {
//                                Log.d(TAG,"pinning");
//                                ParseObject.pinAllInBackground("feed_collegeQuery", parseObjects);
//                            }
//                        });
//                    }
                    Collections.shuffle(feedList);
                    if(mAdapter == null){
                        mAdapter = new FeedCategoryAdapter(feedList);
                        recyclerView.setAdapter(mAdapter);
                    }
                    else{
                        mAdapter.notifyDataSetChanged();
                    }
                    if(swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                } else {
                    if(swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Log.e(TAG, "Getting feed query broke");
                }
            }
        });

//        ParseQuery<ParseObject> aroundQuery = ParseQuery.getQuery(FEED_TABLE).setLimit(10);
//        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
//        queries.add(interestQuery);
//        queries.add(collegeQuery);
//        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
//        mainQuery.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> results, ParseException e) {
//                if(e==null){
//                    Collections.shuffle(results);
//                    mAdapter = new FeedCategoryAdapter(results);
//                    recyclerView.setAdapter(mAdapter);
//                    if(swipeRefreshLayout.isRefreshing())
//                        swipeRefreshLayout.setRefreshing(false);
//                }
//                else{
//                    e.printStackTrace();
//                    if(swipeRefreshLayout.isRefreshing()){
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                }
//            }
//        });
    }

    private static class FeedCategoryAdapter extends RecyclerView.Adapter<FeedCategoryAdapter.FeedCategoryViewHolder> {

        List<ParseObject> feedData = new ArrayList();

        public FeedCategoryAdapter(List<ParseObject> feedList){
            feedData = feedList;
        }

        @Override
        public FeedCategoryAdapter.FeedCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView view = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_category_item, parent, false);
            return new FeedCategoryAdapter.FeedCategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final FeedCategoryViewHolder holder, int position) {

            final ParseObject object = feedData.get(position);
            holder.mTextView.setText(object.getString(KEY_TITLE));
            Ion.with(holder.mImageView).load(object.getString(KEY_IMAGE_URL));
            Ion.with(context).load(object.getString(KEY_IMAGE_URL)).asBitmap().setCallback(new FutureCallback<Bitmap>() {
                @Override
                public void onCompleted(final Exception e, Bitmap result) {
                    if (result != null) {
                        Palette.generateAsync(result, new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                int bgColor = palette.getLightMutedColor(R.color.light_white_);
                                int vibcolor = palette.getLightVibrantColor(R.color.light_white_);
                                int vibdark = palette.getDarkVibrantColor(R.color.accent_material_dark);
                                holder.frameLayout.setBackgroundColor(vibcolor);
                                holder.mTextView.setTextColor(bgColor);
                                holder.mFeedTag.setTextColor(vibdark);
                                //Log.e("Yogesh", "aa ja bc");
                            }
                        });
                    }
                }
            });

            /*Ion.with(holder.mImageView).load(object.getString(KEY_IMAGE_URL)).setCallback(new FutureCallback<ImageView>() {
                @Override
                public void onCompleted(Exception e, ImageView result) {
                    BitmapDrawable drawable = (BitmapDrawable) result.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            int bgColor = palette.getLightMutedColor(R.color.light_white_);
                            holder.frameLayout.setBackgroundColor(bgColor);
                            if(DEBUG) Log.d("Yogesh", "aa gaya bc");
                        }
                    });
                }
            });*/

            if(object.getClassName().equalsIgnoreCase("Feed"))
                holder.mFeedTag.setText("Interests");
            else if(object.getClassName().equalsIgnoreCase("Events"))
                holder.mFeedTag.setText("Events");
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uriString = object.getString(KEY_LINK);
                    if (!uriString.startsWith("http")) {
                        uriString = "http://" + uriString;
                    }
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
            if (feedData == null)
                return 0;
            return feedData.size();
        }

        public static class FeedCategoryViewHolder extends RecyclerView.ViewHolder {
            public CardView view;
            public TextView mTextView;
            public ImageView mImageView;
            public TextView mFeedTag;
            public FrameLayout frameLayout;

            public FeedCategoryViewHolder(CardView v) {
                super(v);
                this.view = v;
                mTextView = (TextView) this.view.findViewById(R.id.feed_item_text_view);
                mImageView = (ImageView) this.view.findViewById(R.id.feed_item_image);
                mFeedTag = (TextView) this.view.findViewById(R.id.feed_category_tag);
                frameLayout = (FrameLayout) this.view.findViewById(R.id.feeds_frame);
            }
        }
    }

}
