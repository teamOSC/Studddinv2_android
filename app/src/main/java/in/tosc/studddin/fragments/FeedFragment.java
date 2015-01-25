package in.tosc.studddin.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import in.tosc.studddin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment implements View.OnKeyListener{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private static RecyclerView.LayoutManager mVerticalLayoutManager;
    View rootView;

    private static Context context;

    private static final String TAG = FeedFragment.class.getName();

    private EditText searchEditText;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    // TODO: Rename and change types and number of parameters
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

        new ReadFromJSON(getJSON()).execute();
        return rootView;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (v.getId() == R.id.feed_search) {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        Toast.makeText(context, "Search", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        break;
                }
            }
        }
        return false;
    }

    private static class FeedRootAdapter extends RecyclerView.Adapter<FeedRootAdapter.ViewHolder> {
        private FeedRootWrapper[] mDataset;
        private JSONArray jsonArray;

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

        public FeedRootAdapter(FeedRootWrapper[] dataSet, JSONArray jsonArray) {
            mDataset = dataSet;
            this.jsonArray = jsonArray;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public FeedRootAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_root_list_card_view, parent, false);
            ViewHolder vh = new ViewHolder(v);
            RecyclerView.LayoutManager mHorizontalLayoutManager = new LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false);
            CategoryWrapper[] categoryWrappers = new CategoryWrapper[6];
            for (int i = 0; i < 6; ++i) {
                categoryWrappers[i] = new CategoryWrapper();
            }
            FeedCategoryAdapter mFeedCategoryAdapter = new FeedCategoryAdapter(categoryWrappers, jsonArray);
            vh.mHorizontalRecyclerView.setAdapter(mFeedCategoryAdapter);
            vh.mHorizontalRecyclerView.setLayoutManager(mHorizontalLayoutManager);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mDataset[position].dummyString);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }

    private static class FeedCategoryAdapter extends RecyclerView.Adapter<FeedCategoryAdapter.FeedCategoryViewHolder> {

        private CategoryWrapper[] mDataSet;
        private JSONArray jsonArray;

        public FeedCategoryAdapter(CategoryWrapper[] wrappers, JSONArray jsonArray) {
            this.mDataSet = wrappers;
            this.jsonArray = jsonArray;
            Log.d(TAG, "jsonArray = " + jsonArray.toString());
        }

        public static class FeedCategoryViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public LinearLayout view;
            public TextView mTextView;
            public FeedCategoryViewHolder(LinearLayout v) {
                super(v);
                this.view = v;
                mTextView = (TextView) this.view.findViewById(R.id.feed_item_text_view);
            }
        }

        @Override
        public FeedCategoryAdapter.FeedCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_category_item, parent, false);
            FeedCategoryAdapter.FeedCategoryViewHolder vh = new FeedCategoryAdapter.FeedCategoryViewHolder(view);
            Log.d(TAG, "onCreateViewHolder");
            return vh;
        }

        @Override
        public void onBindViewHolder(FeedCategoryViewHolder holder, int position) {
            try {
                JSONObject mJsonObject = (JSONObject) jsonArray.get(position);
                holder.mTextView.setText("link" + mJsonObject.getString("link") + "\n"
                        + "title" + mJsonObject.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mDataSet.length;
        }
    }

    private static class FeedRootWrapper {
        public String dummyString;

        public FeedRootWrapper(String string) {
            this.dummyString = string;
        }
    }

    private static class CategoryWrapper {
        public CategoryWrapper() {

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
                FeedRootWrapper[] wrappers = new FeedRootWrapper[3];
                for (int i = 0; i < 3; ++i) {
                    int resourceId = 0;
                    try {
                        resourceId = getCategoryResource(i);
                    } catch (UnsupportedOperationException e) {
                        Toast.makeText(getActivity(), "Unsupported Operation", Toast.LENGTH_SHORT).show();
                    }
                    wrappers[i] = new FeedRootWrapper(getString(resourceId));
                }
                mAdapter = new FeedRootAdapter(wrappers, mJsonArray);
                mRecyclerView.setLayoutManager(mVerticalLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
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
    }
}
