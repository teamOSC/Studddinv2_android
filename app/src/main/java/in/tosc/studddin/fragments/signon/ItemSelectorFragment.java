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
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.externalapi.ParseTables;

/**
 * Created by root on 8/4/15.
 */
public class ItemSelectorFragment extends Fragment {

    View rootView;

    Bundle incomingBundle;

    public static final int TYPE_INTEREST = 0;
    public static final int TYPE_COLLEGE = 1;

    public static final String TYPE = "type";

    private RecyclerView itemRecyclerView;
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
        rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        parentActivity = getActivity();
        incomingBundle = getArguments();

        itemRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_interests);
        itemRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(parentActivity);
        itemRecyclerView.setLayoutManager(mLayoutManager);

        int type = incomingBundle.getInt(TYPE);
        switch (type) {
            case TYPE_INTEREST:
                //TODO: Get the list of interest and inflate in the recycler view
                break;
            case TYPE_COLLEGE:
                //TODO: Get the list of college and inflate in the recycler view
                break;
            default:
                throw new UnsupportedOperationException("Unknown ItemSelector Type");
        }
        return rootView;
    }

    private void inflateInterests(final int type) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTables.Tables.Interests);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                mAdapter = new ItemAdapter(list, type);
                itemRecyclerView.setAdapter(mAdapter);
            }
        });
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
                    //TODO: get college name
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
