package in.tosc.studddin.fragments.signon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.tosc.studddin.R;

/**
 * Created by root on 8/4/15.
 */
public class ItemSelectorFragment extends Fragment {

    View rootView;

    public ItemSelectorFragment() {
        // Required empty public constructor
    }

    public static ItemSelectorFragment newInstance(Bundle bundle) {
        ItemSelectorFragment fragment = new ItemSelectorFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        return rootView;
    }
}
