package in.tosc.studddin.fragments.signon;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.ui.ProgressBarCircular;

/**
 * Class used for selection of Interests and College during the SignUp
 */
public class ItemSelectorFragment extends Fragment {

    View rootView;

    Bundle incomingBundle;

    public static final int TYPE_INTEREST = 0;
    public static final int TYPE_COLLEGE = 1;

    public static final String TYPE = "type";

    private RecyclerView itemRecyclerView;
    private ProgressBar progressBar;

    private ItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Activity parentActivity;

    public ItemSelectorFragment() {
        // Required empty public constructor
    }

    public static ItemSelectorFragment newInstance(Bundle bundle) {
        ItemSelectorFragment fragment = new ItemSelectorFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_item_selector, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressbar_item_selector);
        itemRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_interests);

        parentActivity = getActivity();
        incomingBundle = getArguments();

        int type = incomingBundle.getInt(TYPE);
        switch (type) {
            case TYPE_INTEREST:
                inflateInterests();
                break;
            case TYPE_COLLEGE:
                inflateColleges();
                break;
            default:
                throw new UnsupportedOperationException("Unknown ItemSelector Type");
        }
        return rootView;
    }

    private void inflateInterests() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTables.Interests._NAME);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                showItemsList(list, TYPE_INTEREST);
            }
        });
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
                showItemsList(list, TYPE_COLLEGE);
            }
        });
    }

    private void showItemsList(List<ParseObject> list, int type) {
        progressBar.setVisibility(View.GONE);
        itemRecyclerView.setVisibility(View.VISIBLE);

        itemRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(parentActivity);
        itemRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ItemAdapter(list, type);
        itemRecyclerView.setAdapter(mAdapter);
    }

    public static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
        private List<ParseObject> mainList;
        private int type;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public CheckBox mCheckBox;
            public ViewHolder(CheckBox v) {
                super(v);
                mCheckBox = v;
            }
        }

        public ItemAdapter(List<ParseObject> myDataset, int type) {
            mainList = myDataset;
            this.type = type;
        }

        public void updateDataSet(List<ParseObject> mDataSet) {
            this.mainList = mDataSet;
        }

        @Override
        public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_selector, parent, false);
            CheckBox mCheckBox = (CheckBox) v.findViewById(R.id.checkbox_interest);
            ViewHolder vh = new ViewHolder(mCheckBox);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String text = "";
            switch (type) {
                case TYPE_INTEREST:
                    text = mainList.get(position).getString(ParseTables.Interests.NAME);
                    break;
                case TYPE_COLLEGE:
                    text = mainList.get(position).getString(ParseTables.College.NAME);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown ItemSelector Type");

            }
            holder.mCheckBox.setText(text);
        }

        @Override
        public int getItemCount() {
            return mainList.size();
        }
    }
}
