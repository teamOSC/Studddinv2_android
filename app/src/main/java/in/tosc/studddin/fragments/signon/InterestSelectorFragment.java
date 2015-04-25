package in.tosc.studddin.fragments.signon;

import android.os.Bundle;

/**
 * Created by root on 21/4/15.
 */
public class InterestSelectorFragment extends ItemSelectorFragment {

    public static InterestSelectorFragment newInstance(Bundle bundle) {
        InterestSelectorFragment fragment = new InterestSelectorFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

}
