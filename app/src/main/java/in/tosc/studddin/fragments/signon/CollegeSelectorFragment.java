package in.tosc.studddin.fragments.signon;

import android.os.Bundle;

/**
 * Created by root on 21/4/15.
 */
public class CollegeSelectorFragment extends ItemSelectorFragment {
    
    public static CollegeSelectorFragment newInstance(Bundle bundle) {
        CollegeSelectorFragment fragment = new CollegeSelectorFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getMaxSelectableItems() {
        return 1;
    }
}
