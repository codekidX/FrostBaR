package com.ashish.frostbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ashish.frostbar.Utils.MyPreference;


public class DeviceSelectionActivity extends Activity implements AdapterView.OnItemSelectedListener {

    MyPreference myPreference;
    private DeviceSelectionActivity deviceSelectionActivity;


    public String grand = "gd";
    public String xperiaS = "xs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_selection);

        Spinner spinner = (Spinner) findViewById(R.id.device_spinner);

        ArrayAdapter<CharSequence> deviceAdapter = ArrayAdapter.createFromResource(this, R.array.device_array, android.R.layout.simple_spinner_item);
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(deviceAdapter);
        spinner.setOnItemSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        setDevicePref(i);



    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setDevicePref(int id) {

        SharedPreferences wwwpref = getSharedPreferences(MyPreference.DEVICE_PREF, 0);
        SharedPreferences.Editor editor = wwwpref.edit();
        if(id == 0) {

            editor.putString("device", "");
        }

        else if(id == 1) {

            editor.putString("device", grand);


        } else if(id == 2) {
            editor.putString("device", xperiaS);

        }
        editor.apply();

    }

    public void goToMainPref(View view) {

        SharedPreferences wwwpref = getSharedPreferences(MyPreference.DEVICE_PREF, 0);
        SharedPreferences.Editor editor = wwwpref.edit();
        wwwpref = getSharedPreferences(MyPreference.DEVICE_PREF, 0);
        editor.putBoolean("setup_done", true);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        deviceSelectionActivity.finishActivity(0);

    }
}
