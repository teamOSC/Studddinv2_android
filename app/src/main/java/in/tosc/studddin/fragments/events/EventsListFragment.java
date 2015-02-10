package in.tosc.studddin.fragments.events;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
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
    RecyclerView.Adapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private boolean refresh = false;
    private boolean check_my_events=false;
    ParseImageView expandedImage;
    View v;
    LinearLayout eventMainLayout;
    LinearLayout emptyEvent;
    ArrayList<Bitmap> eventImages;

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
        v = inflater.inflate(R.layout.fragment_events_list, container, false);
        eventImages = new ArrayList<>();
        eventlist = (RecyclerView) v.findViewById(R.id.listviewevents);
        eventMainLayout = (LinearLayout) v.findViewById(R.id.events_main_list);
        emptyEvent = (LinearLayout) v.findViewById(R.id.empty_events);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        eventlist.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh = true;
                fetchData();
            }
        });
        fetchData();

        return v;
    }

    public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> implements View.OnClickListener {

        private int expandedPosition = -1;
        private List<ParseObject> events;

        public EventAdapter(List<ParseObject> events) {
            this.events = events;
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
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.event_name.setText((String)events.get(position).get(ParseTables.Events.TITLE));
            holder.event_description.setText((String)events.get(position).get(ParseTables.Events.DESCRIPTION));
            holder.event_type.setText((String)events.get(position).get(ParseTables.Events.TYPE));
            holder.event_date.setText(events.get(position).get(ParseTables.Events.DATE)+" "+events.get(position).get(ParseTables.Events.TIME));
            holder.event_creator.setText((String)events.get(position).get(ParseTables.Events.CREATED_BY));
            holder.event_location.setText((String)events.get(position).get(ParseTables.Events.LOCATION_DES));
            holder.event_image.setParseFile(events.get(position).getParseFile(ParseTables.Events.IMAGE));
            holder.event_image.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    eventImages.add(bitmap);
                }
            });
            holder.event_contact.setText((String)events.get(position).get(ParseTables.Events.CONTACT));
            holder.event_url.setText((String)events.get(position).get(ParseTables.Events.URL));
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
            return events.size();
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
                        ParseObject event = events.get(getPosition());
                        event.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    eventImages.remove(getPosition());
                                    fetchData();
                                }
                                else{
                                    Toast.makeText(getActivity().getApplicationContext(), "Internet Connection Problem", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                event_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        zoomIn(eventImages.get(getPosition()));
                        Log.d("Image", events.get(getPosition()).getString(ParseTables.Events.TITLE));
                    }
                });
            }
        }
    }

    public void fetchData(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                "Events");
        query.orderByAscending(ParseTables.Events.CREATED_AT);
        if(check_my_events){
            query.whereEqualTo(ParseTables.Events.CREATED_BY, ParseUser.getCurrentUser().getString("NAME"));
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                doneFetching(parseObjects);
            }
        });
    }

    public void doneFetching(List<ParseObject> events){
        adapter = new EventAdapter(events);
        eventlist.setAdapter(adapter);
        if (refresh == true) {
            swipeRefreshLayout.setRefreshing(false);
            refresh = false;
        }
        if(check_my_events && adapter.getItemCount() == 0){
            emptyEvent.setVisibility(View.VISIBLE);
            eventMainLayout.setVisibility(View.GONE);
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

    private void zoomIn(Bitmap bitmap){
        expandedImage = (ParseImageView)v.findViewById(R.id.expanded_image_view);
        expandedImage.setImageBitmap(bitmap);
        expandedImage.setVisibility(View.VISIBLE);
        eventMainLayout.setVisibility(View.GONE);
        emptyEvent.setVisibility(View.GONE);

        expandedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventMainLayout.setVisibility(View.VISIBLE);
                emptyEvent.setVisibility(View.VISIBLE);
                expandedImage.setVisibility(View.GONE);
            }
        });
    }
}
