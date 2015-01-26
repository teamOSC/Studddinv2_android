package in.tosc.studddin.fragments.events;


import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import in.tosc.studddin.R;
import in.tosc.studddin.fragments.FeedFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsListFragment extends Fragment {

    ExpandableListView eventlist;
    ArrayList<Parent> parents = new ArrayList<Parent>();
    ArrayList<Child> children = new ArrayList<Child>();
    Parent parent;
    Child child;
    MyListAdapter adapter;


    public EventsListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events_list, container, false);
        eventlist = (ExpandableListView)v.findViewById(R.id.listviewevents);
        eventlist.setGroupIndicator(null);
        loadData();
        adapter = new MyListAdapter(getActivity().getApplicationContext());
        eventlist.setAdapter(adapter);
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
            holder.header.setText(p.event_name);
            holder.footer.setText(p.event_date);
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
            Child c = children.get(groupPosition);
            holder.header.setText(c.event_description);
            holder.footer.setText(c.event_time);
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
        String event_name;
        String event_date;
    }

    public class Child{
        String event_description;
        String event_time;
    }

    public void loadData(){

        for(int i = 0; i < parents.size(); i++){
            parents.remove(i);
            children.remove(i);
        }

        for(int i = 0; i < 5; i++){
            parent = new Parent();
            parent.event_name = "Event" + i ;
            parent.event_date = "Date" + i;
            parents.add(i, parent);
            child = new Child();
            child.event_description = "Description" + i;
            child.event_time = "Time" + i;
            children.add(i, child);
        }

    }
}
