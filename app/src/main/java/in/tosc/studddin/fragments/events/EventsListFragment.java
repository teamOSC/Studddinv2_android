package in.tosc.studddin.fragments.events;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.fragments.EventsFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsListFragment extends Fragment {

    RecyclerView eventlist;
    ArrayList<Parent> parents;
    List<ParseObject> listings;
    RecyclerView.Adapter adapter;
    FetchData f;
    SwipeRefreshLayout swipeRefreshLayout;
    private boolean refresh = false;
    private boolean check_my_events=false;

    public EventsListFragment(){

    }

    public static EventsListFragment newInstance(Boolean check){
        EventsListFragment eventsListFragment = new EventsListFragment();
        Bundle b = new Bundle();
        b.putBoolean("check", check);
        eventsListFragment.setArguments(b);
        return eventsListFragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(this.getArguments() != null){
            check_my_events = getArguments().getBoolean("check");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events_list, container, false);
        eventlist = (RecyclerView) v.findViewById(R.id.listviewevents);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        eventlist.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh = true;
                f = new FetchData(check_my_events);
                f.execute();
            }
        });
        f = new FetchData(check_my_events);
        f.execute();
        return v;
    }

    public class Parent {
        private String event_name;
        private String event_date;
        private String event_description;
        private String event_type;
        private String event_user;
        private String event_location;
        private ParseFile event_image;
        private String event_contact;
        private String event_url;

        public String getEvent_contact() {
            return event_contact;
        }

        public void setEvent_contact(String event_contact) {
            this.event_contact = event_contact;
        }

        public String getEvent_url() {
            return event_url;
        }

        public void setEvent_url(String event_url) {
            this.event_url = event_url;
        }

        public ParseFile getEvent_image() {
            return event_image;
        }

        public void setEvent_image(ParseFile event_image) {
            this.event_image = event_image;
        }

        public String getEvent_location() {
            return event_location;
        }

        public void setEvent_location(String event_location) {
            this.event_location = event_location;
        }

        public String getEvent_user() {
            return event_user;
        }

        public void setEvent_user(String event_user) {
            this.event_user = event_user;
        }

        public String getEventName() {
            return event_name;
        }

        public void setEventName(String s) {
            this.event_name = s;
        }

        public String getEventDate() {
            return event_date;
        }

        public void setEventDate(String s) {
            this.event_date = s;
        }

        public String getEventDescription() {
            return event_description;
        }

        public void setEventDescription(String d) {
            this.event_description = d;
        }

        public String getEventType() {
            return event_type;
        }

        public void setEventType(String d) {
            this.event_type = d;
        }

    }

    public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> implements View.OnClickListener {

        private int expandedPosition = -1;
        private ArrayList<Parent> parents;

        public EventAdapter(ArrayList<Parent> parent) {
            parents = parent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView cd = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_events, parent, false);
            ViewHolder viewHolder = new ViewHolder(cd);
            viewHolder.itemView.setOnClickListener(EventAdapter.this);
            viewHolder.itemView.setTag(viewHolder);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.event_name.setText(parents.get(position).getEventName());
            holder.event_description.setText(parents.get(position).getEventDescription());
            holder.event_type.setText(parents.get(position).getEventType());
            holder.event_date.setText(parents.get(position).getEventDate());
            holder.event_creator.setText(parents.get(position).getEvent_user());
            holder.event_location.setText(parents.get(position).getEvent_location());
            holder.event_image.setParseFile(parents.get(position).getEvent_image());
            holder.event_image.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {

                }
            });
            holder.event_contact.setText(parents.get(position).getEvent_contact());
            holder.event_url.setText(parents.get(position).getEvent_url());
            if(check_my_events){
                holder.event_creator.setVisibility(View.GONE);
                holder.event_delete.setVisibility(View.VISIBLE);
            }

            if (position == expandedPosition) {
                holder.expanded_area.setVisibility(View.VISIBLE);
            } else {
                holder.expanded_area.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return parents.size();
        }

        @Override
        public void onClick(View v) {
            final ViewHolder holder = (ViewHolder) v.getTag();
            if (holder.getPosition() == expandedPosition) {
                holder.expanded_area.setVisibility(View.GONE);
                expandedPosition = -1;
            } else {
                if (expandedPosition >= 0) {
                    int prev = expandedPosition;
                    notifyItemChanged(prev);
                }
                expandedPosition = holder.getPosition();
                notifyItemChanged(expandedPosition);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView event_name;
            TextView event_date;
            TextView event_description;
            TextView event_type;
            RelativeLayout expanded_area;
            TextView event_creator;
            TextView event_location;
            ParseImageView event_image;
            Button event_delete;
            TextView event_contact;
            TextView event_url;

            public ViewHolder(View itemView) {
                super(itemView);
                this.event_name = (TextView) itemView.findViewById(R.id.event_name);
                this.event_date = (TextView) itemView.findViewById(R.id.event_date);
                this.event_description = (TextView) itemView.findViewById(R.id.event_description);
                this.event_type = (TextView) itemView.findViewById(R.id.event_type);
                this.expanded_area = (RelativeLayout) itemView.findViewById(R.id.expanded_area);
                this.event_creator = (TextView) itemView.findViewById(R.id.event_creator);
                this.event_location = (TextView) itemView.findViewById(R.id.event_location);
                this.event_delete = (Button) itemView.findViewById(R.id.event_delete);
                this.event_image = (ParseImageView)itemView.findViewById(R.id.event_image);
                this.event_contact = (TextView) itemView.findViewById(R.id.event_contact);
                this.event_url = (TextView) itemView.findViewById(R.id.event_url);
                event_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseObject event = listings.get(getPosition());
                        event.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    new FetchData(check_my_events).execute();
                                }
                                else{
                                    Toast.makeText(getActivity().getApplicationContext(), "Internet Connection Problem", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private class FetchData extends AsyncTask<Void, Void, Void> {
        private boolean check;

        public FetchData(Boolean check_myevents){
            check = check_myevents;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            adapter = new EventAdapter(parents);
            eventlist.setAdapter(adapter);
            if (refresh == true) {
                swipeRefreshLayout.setRefreshing(false);
                refresh = false;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            parents = new ArrayList<Parent>();
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                    "Events");
            query.orderByAscending(ParseTables.Events.CREATED_AT);
            if(check){
                query.whereEqualTo(ParseTables.Events.CREATED_BY, ParseUser.getCurrentUser().getString("NAME"));
            }
            try {
                listings = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (ParseObject listing : listings) {
                Parent parent = new Parent();
                parent.setEventName((String) listing.get(ParseTables.Events.TITLE));
                parent.setEventDate(listing.get(ParseTables.Events.DATE) + "  " + listing.get(ParseTables.Events.TIME));
                parent.setEventDescription((String) listing.get(ParseTables.Events.DESCRIPTION));
                parent.setEventType((String) listing.get(ParseTables.Events.TYPE));
                parent.setEvent_user((String) listing.get(ParseTables.Events.CREATED_BY));
                parent.setEvent_location((String) listing.get(ParseTables.Events.LOCATION_DES));
                parent.setEvent_image(listing.getParseFile(ParseTables.Events.IMAGE));
                parent.setEvent_contact((String) listing.get(ParseTables.Events.CONTACT));
                parent.setEvent_url((String) listing.get(ParseTables.Events.URL));
                parents.add(parent);
            }
            return null;
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.events_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.event_add:
                EventsFragment eFragment = (EventsFragment) getParentFragment();
                eFragment.goToOtherFragment(1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
