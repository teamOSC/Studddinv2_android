package in.tosc.studddin.fragments.signon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by root on 21/4/15.
 */
public class CollegeSelectorFragment extends Fragment implements TextWatcher {

    View rootView;
    private Activity parentActivity;
    private RecyclerView collegeRecyclerView;

    private CollegeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressBar progressBar;

    private FloatingActionButton submitButton;
    private ProgressDialog progressDialog;

    private EditText collegeSearchEditText;

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
        parentActivity = getActivity();

        progressDialog = new ProgressDialog(parentActivity);
        progressDialog.setCancelable(false);

        collegeSearchEditText = (EditText) rootView.findViewById(R.id.edit_text_college);
        collegeSearchEditText.addTextChangedListener(this);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressbar_college_selector);
        submitButton = (FloatingActionButton) rootView.findViewById(R.id.button_college_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                pushDataToParse();
            }
        });

        mLayoutManager = new LinearLayoutManager(parentActivity);
        collegeRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_college);
        collegeRecyclerView.setHasFixedSize(true);
        collegeRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CollegeAdapter(new ArrayList<ParseObject>());
        collegeRecyclerView.setAdapter(mAdapter);
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

    public void pushDataToParse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ParseObject selectedCollege = mAdapter.getSelectedCollege();
                if (selectedCollege == null) {
                    throw new NullPointerException("selected college hi null hai.");
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    Log.d(TAG, "current user is not null");
                    ParseRelation<ParseUser> relation = selectedCollege
                            .getRelation(ParseTables.Interests.USERS);
                    relation.add(currentUser);
                    Log.d(TAG, "Relation added");
                    try {
                        selectedCollege.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    currentUser.put(ParseTables.Users.FULLY_REGISTERED, true);
                    currentUser.put(ParseTables.Users.INSTITUTE, selectedCollege);
                    Log.d(TAG, "saving user");
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

    private void showItemsList(List<ParseObject> list) {

        progressBar.setVisibility(View.GONE);
        collegeRecyclerView.setVisibility(View.VISIBLE);
        Log.d(TAG, "Show recycler view");

        mAdapter.updateDataSet(list, true);
        mAdapter.notifyDataSetChanged();
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

    public class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.ViewHolder>
            implements Filterable {
        private List<ParseObject> mainList;
        private List<ParseObject> mDataset;
        //        private SparseArray<Boolean> selectedList = new SparseArray();
        private ParseObject selectedCollege = null;

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
                    selectedCollege = mDataset.get(getPosition());
                } else {
                    selectedCollege = null;
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
            holder.mCheckBox.setText(mDataset.get(position).getString(ParseTables.College.NAME));
            if (selectedCollege != null && selectedCollege.equals(mDataset.get(position))) {
                holder.mCheckBox.setChecked(true);
            } else {
                holder.mCheckBox.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public ParseObject getSelectedCollege() {
            return selectedCollege;
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
            mAdapter.updateDataSet((ArrayList) results.values, false);
            mAdapter.notifyDataSetChanged();
        }
    }
}
