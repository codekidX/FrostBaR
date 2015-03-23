package com.ashish.frostbar;


import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity implements PreferenceScreen.OnPreferenceChangeListener {

    public static String THEME_FLAG = "no";
    public static final String THEME_PREF="dark_cpref";

    private CheckBoxPreference mDarkThemePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_setting);
        if(THEME_FLAG.equals("no")){

            setTheme(R.style.AppTheme);
        }
        else {

            setTheme(R.style.DarkTheme);
        }


        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            mDarkThemePref = (CheckBoxPreference) preferenceScreen.findPreference(THEME_PREF);
        }
        if (mDarkThemePref != null) {
            mDarkThemePref.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if(preference == mDarkThemePref) {

            boolean value = (Boolean) newValue;
            if(value) {

                THEME_FLAG = "yes";

            }
            else {

                THEME_FLAG = "no";

            }

        }

        return true;
    }
}
