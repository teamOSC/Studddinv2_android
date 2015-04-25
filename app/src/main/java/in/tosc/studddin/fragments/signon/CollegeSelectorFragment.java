package in.tosc.studddin.fragments.signon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.externalapi.ParseTables;

/**
 * Created by root on 21/4/15.
 */
public class CollegeSelectorFragment extends Fragment {

    View rootView;
    private RecyclerView collegeRecyclerView;

    private CollegeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressBar progressBar;

    private static final String TAG = "CollegeSelectorFragment";
    
    public static CollegeSelectorFragment newInstance(Bundle bundle) {
        CollegeSelectorFragment fragment = new CollegeSelectorFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_college_selector, container, false);
        collegeRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_college);

        mAdapter = new CollegeAdapter(new ArrayList<ParseObject>());
        inflateColleges();
        return rootView;
    }

    private void inflateColleges() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTables.College._NAME);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                showItemsList(list);
            }
        });
    }

    private void showItemsList(List<ParseObject> list) {

        progressBar.setVisibility(View.GONE);
        collegeRecyclerView.setVisibility(View.VISIBLE);
        Log.d(TAG, "Show recycler view");

        mAdapter.updateDataSet(list, true);
        mAdapter.notifyDataSetChanged();
    }

    public class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.ViewHolder>
            implements Filterable {
        private List<ParseObject> mainList;
        private List<ParseObject> mDataset;
        private SparseArray<Boolean> selectedList = new SparseArray();

        @Override
        public Filter getFilter() {
            return new CollegeFilter();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements
                CheckBox.OnClickListener {
            public CheckBox mCheckBox;
            public ViewHolder(LinearLayout v) {
                super(v);
                mCheckBox = (CheckBox) v.findViewById(R.id.checkbox_item);
                mCheckBox.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Log.d(TAG, "checked position = " + getPosition());
                if (mCheckBox.isChecked()) {
                    selectedList.put(mainList.indexOf(mDataset.get(getPosition())), true);
                } else {
                    selectedList.put(mainList.indexOf(mDataset.get(getPosition())), false);
                }
            }
        }

        public CollegeAdapter(List<ParseObject> mDataset) {
            mainList = mDataset;
            this.mDataset = mDataset;
        }

        public void updateDataSet(List<ParseObject> mDataSet, boolean initialize) {
            this.mDataset = mDataSet;
            if (initialize) {
                this.mainList = mDataSet;
            }
        }

        @Override
        public CollegeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_selector, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public SparseArray<Boolean> getSelectedList() {
            return selectedList;
        }

        public List<ParseObject> getDataSet() {
            return mDataset;
        }

        public List<ParseObject> getMainList() {
            return mainList;
        }
    }

    private class CollegeFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            List<ParseObject> mainList = mAdapter.getMainList();
            if (constraint == null || constraint.length() == 0) {
                results.values = mainList;
                results.count = mainList.size();
            } else {
                ArrayList<ParseObject> filteredObjects = new ArrayList();
                String pattern = ((String) constraint).toLowerCase();
                for (ParseObject parseObject : mainList) {
                    String item = parseObject.getString(ParseTables.College.NAME);
                    if (item.toLowerCase().contains(pattern)) {
                        filteredObjects.add(parseObject);
                    }
                }
                results.values = filteredObjects;
                results.count = filteredObjects.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mAdapter.updateDataSet((ArrayList)results.values, false);
            mAdapter.notifyDataSetChanged();
        }
    }
}
