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

import java.util.ArrayList;

import in.tosc.studddin.R;

/**
 * Created by root on 8/4/15.
 */
public class ItemSelectorFragment extends Fragment {

    View rootView;

    Bundle incomingBundle;

    public static final int TYPE_INTEREST = 0;

    public static final String TYPE = "type";

    private RecyclerView interestsRecyclerView;
    private RecyclerView.Adapter interestsAdapter;
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

        interestsRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_interests);
        interestsRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(parentActivity);
        interestsRecyclerView.setLayoutManager(mLayoutManager);
        return rootView;
    }

    public static class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.ViewHolder> {
        private ArrayList<String> interests;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public CheckBox mCheckBox;
            public ViewHolder(CheckBox v) {
                super(v);
                mCheckBox = v;
            }
        }

        public InterestsAdapter(ArrayList<String> myDataset) {
            interests = myDataset;
        }

        @Override
        public InterestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_selector, parent, false);
            CheckBox mCheckBox = (CheckBox) v.findViewById(R.id.checkbox_interest);
            ViewHolder vh = new ViewHolder(mCheckBox);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mCheckBox.setText(interests.get(position));
        }

        @Override
        public int getItemCount() {
            return interests.size();
        }
    }
}
