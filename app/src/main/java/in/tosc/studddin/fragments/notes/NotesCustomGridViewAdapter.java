package in.tosc.studddin.fragments.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.tosc.studddin.R;

/**
 * Created by raghav on 25/01/15.
 */
public class NotesCustomGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> notesCollegeName, notesBranchName, notesTopicName, notesSubjectName;




    public NotesCustomGridViewAdapter(Context c, ArrayList<String> notesCollegeName, ArrayList<String> notesBranchName, ArrayList<String> notesTopicName, ArrayList<String> notesSubjectName){

        mContext = c;
        this.notesBranchName = notesBranchName;
        this.notesCollegeName = notesCollegeName;
        this.notesSubjectName = notesSubjectName;
        this.notesTopicName = notesTopicName;


    }
    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        grid = new View(mContext);
        if (convertView == null) {
            if(position >= 0 && position < 5) {



                grid = inflater.inflate(R.layout.notes_search_gridview_item, null);
                TextView branchNameTxtView = (TextView) grid.findViewById(R.id.notes_gridview_branchname);
                TextView subjectNameTxtView = (TextView) grid.findViewById(R.id.notes_gridview_subjectname);
                TextView topicNameTxtView = (TextView) grid.findViewById(R.id.notes_gridview_topicname);


                branchNameTxtView.setText(notesBranchName.get(0));
                topicNameTxtView.setText(notesTopicName.get(0));
                subjectNameTxtView.setText(notesSubjectName.get(0));

            }

        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}
