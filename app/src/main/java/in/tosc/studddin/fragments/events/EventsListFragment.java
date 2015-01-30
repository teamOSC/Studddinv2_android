package in.tosc.studddin.fragments.events;


import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.tosc.studddin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsListFragment extends Fragment {

    RecyclerView eventlist;
    ArrayList<Parent> parents;
    List<ParseObject> listings;
    RecyclerView.Adapter adapter;
    FetchData f;
    private boolean refresh = false;
    SwipeRefreshLayout swipeRefreshLayout;

    public EventsListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events_list, container, false);
        eventlist = (RecyclerView)v.findViewById(R.id.listviewevents);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        eventlist.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh = true;
                f = new FetchData();
                f.execute();
            }
        });
        f = new FetchData();
        f.execute();
        return v;
    }

    public class Parent{
        private String event_name;
        private Date event_date;
        private String event_description;
        private String event_type;


        public String getEventName(){
            return event_name;
        }

        public Date getEventDate(){
            return event_date;
        }

        public void setEventName(String s){
            this.event_name = s;
        }

        public void setEventDate(Date s){
            this.event_date = s;
        }

        public String getEventDescription(){
            return event_description;
        }

        public void setEventDescription(String d){
            this.event_description = d;
        }

        public String getEventType(){
            return event_type;
        }

        public void setEventType(String d){
            this.event_type = d;
        }

    }

    public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> implements View.OnClickListener{

        private int expandedPosition = -1;
        private ArrayList<Parent> parents;

        public EventAdapter(ArrayList<Parent> parent){
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
            if(holder.getPosition() == expandedPosition){
                holder.expanded_area.setVisibility(View.GONE);
                expandedPosition = -1;
            }else {
                if (expandedPosition >= 0) {
                    int prev = expandedPosition;
                    notifyItemChanged(prev);
                }
                expandedPosition = holder.getPosition();
                notifyItemChanged(expandedPosition);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView event_name;
            TextView event_date;
            TextView event_description;
            TextView event_type;
            RelativeLayout expanded_area;

            public ViewHolder(View itemView) {
                super(itemView);
                this.event_name = (TextView) itemView.findViewById(R.id.event_name);
                this.event_date = (TextView) itemView.findViewById(R.id.event_date);
                this.event_description = (TextView) itemView.findViewById(R.id.event_description);
                this.event_type = (TextView) itemView.findViewById(R.id.event_type);
                this.expanded_area = (RelativeLayout) itemView.findViewById(R.id.expanded_area);
            }
        }
    }

    private class FetchData extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            adapter = new EventAdapter(parents);
            eventlist.setAdapter(adapter);
            if(refresh == true){
                swipeRefreshLayout.setRefreshing(false);
                refresh = false;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            parents = new ArrayList<Parent>();
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                    "Events");
            query.orderByAscending("createdAt");
            try {
                listings = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (ParseObject listing : listings) {
                Parent parent = new Parent();
                parent.setEventName((String) listing.get("title"));
                parent.setEventDate((Date) listing.get("createdAt"));
                parent.setEventDescription((String) listing.get("description"));
                parent.setEventType((String) listing.get("type"));
                parents.add(parent);
            }
            return null;
        }
    }

}
