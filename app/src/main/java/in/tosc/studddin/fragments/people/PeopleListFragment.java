package in.tosc.studddin.fragments.people;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.util.ArrayList;

import in.tosc.studddin.ApplicationWrapper;
import in.tosc.studddin.R;
import in.tosc.studddin.ui.ParseCircularImageView;
import in.tosc.studddin.ui.ProgressBarCircular;

/**
 * Created by championswimmer on 26/4/15.
 */
public class PeopleListFragment extends Fragment {
    ProgressBarCircular progressBar;

    String currentuseremail = "";
    String currentuserinterests = "";
    String currentuserinstituition = "";
    String currentusername = "";
    String currentuserqualification = "";
    String currentuser = "";

    ArrayList<EachRow3> listOfPeople = new ArrayList<EachRow3>();


    EachRow3 each;
    MyAdapter3 q;
    ListView lv;


    public PeopleListFragment() {

    }

    protected class EachRow3 {
        String cname = "";
        String cinterests = "";
        String cdistance = "";
        String cqualification = "";
        String cinstituition = "";
        String cusername = "";
        String cauthData = "";

        Bitmap cbmp;
        ParseFile fileObject;
    }

    class MyAdapter3 extends ArrayAdapter<EachRow3> {
        LayoutInflater inflat;
        ViewHolder holder;

        public MyAdapter3(Context context, int textViewResourceId,
                          ArrayList<EachRow3> objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
            inflat = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final int pos = position;

            if (convertView == null) {
                convertView = inflat.inflate(R.layout.listview_people, null);
                holder = new ViewHolder();
                holder.textname = (TextView) convertView.findViewById(R.id.people_name);
                holder.textinterests = (TextView) convertView.findViewById(R.id.people_interests);
                holder.textinstituition = (TextView) convertView.findViewById(R.id.people_institute);
                holder.textdistance = (TextView) convertView.findViewById(R.id.people_distance);
                holder.textqualification = (TextView) convertView.findViewById(R.id.people_qualification);
                holder.userimg = (ParseCircularImageView) convertView.findViewById(R.id.people_userimg);

                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            EachRow3 row = getItem(position);

            holder.textname.setText(row.cname);
            holder.textinterests.setText(row.cinterests);
            holder.textinstituition.setText(row.cinstituition);
            holder.textdistance.setText(row.cdistance);
            holder.textqualification.setText(row.cqualification);
            holder.textdistance.setText(row.cdistance);


            if (row.fileObject != null) {
                row.fileObject
                        .getDataInBackground(new GetDataCallback() {

                            public void done(byte[] data,
                                             ParseException e) {
                                if (e == null) {
                                    if (ApplicationWrapper.LOG_DEBUG) Log.d("test",
                                            "We've got data in data.");

                                    holder.userimg.setImageBitmap(BitmapFactory
                                            .decodeByteArray(
                                                    data, 0,
                                                    data.length));

                                } else {
                                    if (ApplicationWrapper.LOG_DEBUG)
                                        Log.d("test", "There was a problem downloading the data.");
                                }
                            }
                        });
            } else {
                holder.userimg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_picture_blank_portrait));
            }

            return convertView;
        }

        @Override
        public EachRow3 getItem(int position) {
            // TODO Auto-generated method stub
            return listOfPeople.get(position);
        }

        private class ViewHolder {

            TextView textname;
            TextView textinterests;
            TextView textdistance;
            TextView textinstituition;
            TextView textqualification;

            ParseImageView userimg;

        }

    }


}
