package com.ashish.frostbar.Utils;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Ashish on 21-03-2015.
 */
public class MyPreference extends Activity{

    //declaring constants
    public static final String DEVICE_PREF = "selected_device";

    //declaring device return variables
    public String grand = "gd";
    public String xperiaS = "xs";

    public void setDevicePref(int id) {

        SharedPreferences deviceSettings = getSharedPreferences(DEVICE_PREF, 0);
        SharedPreferences.Editor editor = deviceSettings.edit();

        if(id == 1) {

            editor.putString("GGD", grand);


        } else if(id == 2) {
            editor.putString("XS", xperiaS);

        }
        editor.apply();

    }



}
