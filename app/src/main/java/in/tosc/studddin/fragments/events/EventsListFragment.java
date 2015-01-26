package in.tosc.studddin.fragments.events;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import in.tosc.studddin.R;
import in.tosc.studddin.fragments.FeedFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsListFragment extends Fragment {

    ExpandableListView eventlist;
    ArrayList<Parent> parents;
    MyListAdapter adapter;
    List<ParseObject> listings;

    public EventsListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events_list, container, false);
        eventlist = (ExpandableListView)v.findViewById(R.id.listviewevents);
        eventlist.setGroupIndicator(null);
        FetchData f = new FetchData();
        f.execute();
        return v;
    }

    public class MyListAdapter extends BaseExpandableListAdapter{
        LayoutInflater inflator;
        viewHolder holder;

        public MyListAdapter(Context context){
            inflator = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            return parents.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflator.inflate(R.layout.listview_events, null);
                holder = new viewHolder();
                holder.header = (TextView) convertView.findViewById(R.id.event_name);
                holder.footer = (TextView) convertView.findViewById(R.id.event_date);
                convertView.setTag(holder);
            }

            holder = (viewHolder) convertView.getTag();
            Parent p = parents.get(groupPosition);
            holder.header.setText(p.getEventName());
            holder.footer.setText(p.getEventDate() + "");
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflator.inflate(R.layout.listview_events_child, null);
                holder = new viewHolder();
                holder.header = (TextView) convertView.findViewById(R.id.event_description);
                holder.footer = (TextView) convertView.findViewById(R.id.event_time);
                convertView.setTag(holder);
            }

            holder = (viewHolder) convertView.getTag();
            Parent p = parents.get(groupPosition);
            holder.header.setText(p.getEventDescription());
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    public class viewHolder{
        TextView header;
        TextView footer;
    }

    public class Parent{
        private String event_name;
        private Date event_date;

        private String event_description;
        private Date event_time;


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

    }

    private class FetchData extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPostExecute(Void aVoid) {

            adapter = new MyListAdapter(getActivity().getApplicationContext());
            eventlist.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... params) {
            parents = new ArrayList<Parent>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "Events");
                query.orderByAscending("createdAt");
                listings = query.find();
                for(ParseObject listing : listings){
                    Parent parent = new Parent();
                    parent.setEventName((String) listing.get("title"));
                    parent.setEventDate((Date) listing.get("createdAt"));
                    parent.setEventDescription((String) listing.get("description"));
                    parents.add(parent);
                }
            }
            catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }


    }

}
