package in.tosc.studddin.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import in.tosc.studddin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private static RecyclerView.LayoutManager mVerticalLayoutManager;
    View rootView;

    private static Context context;

    private static final String TAG = FeedFragment.class.getName();

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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.feed_recycler_view);

        context = getActivity();

        // use a linear layout manager
        mVerticalLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mVerticalLayoutManager);

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

        // specify an adapter (see also next example)
        mAdapter = new FeedRootAdapter(wrappers);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private static class FeedRootAdapter extends RecyclerView.Adapter<FeedRootAdapter.ViewHolder> {
        private FeedRootWrapper[] mDataset;

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

        // Provide a suitable constructor (depends on the kind of dataset)
        public FeedRootAdapter(FeedRootWrapper[] dataSet) {
            mDataset = dataSet;
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
            FeedCategoryAdapter mFeedCategoryAdapter = new FeedCategoryAdapter(categoryWrappers);
            vh.mHorizontalRecyclerView.setAdapter(mFeedCategoryAdapter);
            vh.mHorizontalRecyclerView.setLayoutManager(mHorizontalLayoutManager);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
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

        public FeedCategoryAdapter(CategoryWrapper[] wrappers) {
            this.mDataSet = wrappers;
        }

        public static class FeedCategoryViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public LinearLayout view;
            public FeedCategoryViewHolder(LinearLayout v) {
                super(v);
                this.view = v;
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
}
