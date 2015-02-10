package in.tosc.studddin.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import in.tosc.studddin.R;
import in.tosc.studddin.fragments.signon.SignupDataFragment;

/**
 * Created by root on 3/2/15.
 */
public class BubbleCompletionView extends TokenCompleteTextView {

    public BubbleCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Object object) {
        String p = (String)object;

        if (p.equals(SignupDataFragment.ADD_NEW_INTEREST)) {
            p = SignupDataFragment.lastEnteredInterest;
        }
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.bubble_token,
                (ViewGroup)this.getParent(), false);
        ((TextView)view.findViewById(R.id.name)).setText(p);
        return view;
    }

    @Override
    protected Object defaultObject(String completionText) {
        Log.d("BubbleCompletionView", "text = " + completionText);
        return completionText;
    }
}
