package in.tosc.studddin.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by yogesh on 2/2/15.
 */
public class CustomType {

    private static HashMap<String, Typeface> typefaces = new HashMap<String, Typeface>();
    public static Typeface getTypeface(Context context, String path){
        if (typefaces.containsKey(path)){
            return typefaces.get(path);
        }
        else{
            Typeface tf = Typeface.createFromAsset(context.getAssets(), path);
            typefaces.put(path, tf);
            return tf;
        }
    }
}