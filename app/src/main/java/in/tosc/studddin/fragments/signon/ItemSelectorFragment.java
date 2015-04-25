package in.tosc.studddin.fragments.signon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import in.tosc.studddin.MainActivity;
import in.tosc.studddin.R;
import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.ui.FloatingActionButton;

/**
 * Class used for selection of Interests and College during the SignUp
 */
public abstract class ItemSelectorFragment extends Fragment implements TextWatcher{

    private static final String TAG = "ItemSelectorFragment";

    View rootView;

    Bundle incomingBundle;

    public static final int TYPE_INTEREST = 0;
    public static final int TYPE_COLLEGE = 1;

    public static final String TYPE = "type";

    private RecyclerView itemRecyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton submitButton;
    private EditText searchEditText;

    private ItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Activity parentActivity;

    private ProgressDialog progressDialog;

    public ItemSelectorFragment() {
        // Required empty public constructor
    }

    private int maxSelectableItems = getMaxSelectableItems();

    public abstract int getMaxSelectableItems();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_item_selector, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressbar_item_selector);
        itemRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_interests);
        submitButton = (FloatingActionButton) rootView.findViewById(R.id.button_item_submit);
        searchEditText = (EditText) rootView.findViewById(R.id.edit_text_interest);
        searchEditText.addTextChangedListener(this);

        parentActivity = getActivity();
        incomingBundle = getArguments();

        final int type = incomingBundle.getInt(TYPE);

        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(parentActivity);
                progressDialog.setMessage("Saving...");
                progressDialog.show();
                pushDataToParse(type);
            }
        });

        itemRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(parentActivity, 2);
        itemRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ItemAdapter(new ArrayList<ParseObject>(), type);
        itemRecyclerView.setAdapter(mAdapter);

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
                Log.d(TAG, "List of size retrned = " + list.size());
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
        Log.d(TAG, "Show recycler view");

        mAdapter.updateDataSet(list, true);
        mAdapter.notifyDataSetChanged();
    }

    public void pushDataToParse(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SparseArray<Boolean> selectedList = mAdapter.getSelectedList();
                if (selectedList == null) {
                    throw new NullPointerException("selected list hi null hai.");
                }
                Log.d(TAG, "size of selected list - " + selectedList.size());
                List<ParseObject> mainList = mAdapter.getDataSet();
                ArrayList<ParseObject> selectedParseObjects = new ArrayList();
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    for (int i = 0; i < mainList.size(); ++i) {
                        if (selectedList.get(i) != null && selectedList.get(i) == true) {
                            selectedParseObjects.add(mainList.get(i));
                        }
                    }
                    if (type == TYPE_INTEREST) {
                        for (ParseObject selectedParseObject : selectedParseObjects) {
                            ParseRelation<ParseUser> relation = selectedParseObject
                                    .getRelation(ParseTables.Interests.USERS);
                            relation.add(currentUser);
                            try {
                                selectedParseObject.save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        currentUser.put(ParseTables.Users.INTERESTS, selectedParseObjects);
                        currentUser.put(ParseTables.Users.FULLY_REGISTERED, true);
                        try {
                            currentUser.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                        parentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                goToMainActivity(parentActivity);
                            }
                        });
                    }
                } else {
                    Toast.makeText(parentActivity, "You are not logged in. Please login.", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    public static void goToMainActivity(Activity act) {
        Intent i = new Intent(act, MainActivity.class);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            Activity activity = act;
            Bundle options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle();
            activity.getWindow().setExitTransition(new Explode().setDuration(1500));
            ActivityCompat.startActivityForResult(activity, i, 0, options);
        } else {
            act.startActivity(i);
        }
        act.finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mAdapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>
            implements Filterable{
        private List<ParseObject> mainList;
        private List<ParseObject> mDataset;
        private SparseArray<Boolean> selectedList = new SparseArray();
        private int type;

        @Override
        public Filter getFilter() {
            return new ItemFilter();
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

        public ItemAdapter(List<ParseObject> mDataset, int type) {
            mainList = mDataset;
            this.mDataset = mDataset;
            this.type = type;
        }

        public void updateDataSet(List<ParseObject> mDataSet, boolean initialize) {
            this.mDataset = mDataSet;
            if (initialize) {
                this.mainList = mDataSet;
            }
        }

        @Override
        public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_selector, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String text = "";
            switch (type) {
                case TYPE_INTEREST:
                    text = mDataset.get(position).getString(ParseTables.Interests.NAME);
                    break;
                case TYPE_COLLEGE:
                    text = mDataset.get(position).getString(ParseTables.College.NAME);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown ItemSelector Type");

            }
            holder.mCheckBox.setText(text);
            int positionInMainList = mainList.indexOf(mDataset.get(position));
            if (selectedList.get(positionInMainList) != null && selectedList.get(positionInMainList)) {
                holder.mCheckBox.setChecked(true);
            } else {
                holder.mCheckBox.setChecked(false);
            }
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

    private class ItemFilter extends Filter {

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
                    String item = parseObject.getString(ParseTables.Interests.NAME);
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
