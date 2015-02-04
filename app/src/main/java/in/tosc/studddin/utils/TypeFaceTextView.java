package in.tosc.studddin.utils;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by yogesh on 2/2/15.
 */
public class TypeFaceTextView extends android.widget.TextView {

    public TypeFaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            /**
             * Searching for the typeface attribute
             */
            if (attrs.getAttributeName(i).equals("typeface")) {
                String typeface = attrs.getAttributeValue(i);
                if (!isInEditMode()) {
                    try {
                        setTypeface(CustomType.getTypeface(context, typeface));
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }
}
