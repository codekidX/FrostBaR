package com.ashish.frostbar;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.ashish.frostbar.Utils.MyPreference;
import com.ashish.frostbar.helper.GalaxyGrandBlocks;
import com.ashish.frostbar.helper.GeneralPurpose;
import com.ashish.frostbar.helper.XperiaSBlocks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends PreferenceActivity implements PreferenceScreen.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    public static final String BACKUP_PREF = "backup_epref";
    public static final String BACKUP_MOD_PREF = "backup_mod_epref";
    public static final String RESTORE_PREF = "restore_lpref";
    public static final String RESTORE_MOD_PREF = "restore_mod_lpref";
    public static final String MSIM_PREF = "msim_spref";
    public static final String REC_PREF = "rec_lpref";
    public static final String BACKUP_REC_PREF = "rec_epref";
    public static final String DELETE_PREF = "delete_pref";
    private static final String TAG = "MainPrefActivity";
    private static String cGetMsim = "getprop persist.radio.multisim.config";
    private static String cSetDualMsim = "setprop persist.radio.multisim.config dsds";
    private static String cSetSingleMsim = "setprop persist.radio.multisim.config none";
    private final String rebootDialog_title = "Reboot Device";
    private final String rebootDialog_message = "Press OK to reboot device";

    private EditTextPreference mBackupPreference;
    private EditTextPreference mBackupModPreference;
    private EditTextPreference mBackupRecoveryPreference;
    private ListPreference mRestorePreference;
    private ListPreference mRestoreModPreference;
    private ListPreference mRecoveryPreference;
    private SwitchPreference mMsimSwitchPreference;
    private Preference mDeletePreference;

    //preferences
    private String deviceName = "";
    private boolean deviceSetup = false;

    public String grand = "gd";
    public String xperiaS = "xs";
    private boolean firstbackup;
    //Shared preference
    SharedPreferences wwwpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        if (SettingsActivity.THEME_FLAG.equals("no")) {

            setTheme(R.style.AppTheme);
        } else {

            setTheme(R.style.DarkTheme);
        }
        createDirectory();
        wwwpref = getSharedPreferences(MyPreference.DEVICE_PREF, 0);

        PreferenceScreen prefScreen = getPreferenceScreen();

        if (prefScreen != null) {
            mBackupPreference = (EditTextPreference) prefScreen.findPreference(BACKUP_PREF);
            mBackupModPreference = (EditTextPreference) prefScreen.findPreference(BACKUP_MOD_PREF);
            mRestorePreference = (ListPreference) prefScreen.findPreference(RESTORE_PREF);
            mRestoreModPreference = (ListPreference) prefScreen.findPreference(RESTORE_MOD_PREF);
            mRecoveryPreference = (ListPreference) prefScreen.findPreference(REC_PREF);
            mBackupRecoveryPreference = (EditTextPreference) prefScreen.findPreference(BACKUP_REC_PREF);


        }
        assert mBackupPreference != null;
        mBackupPreference.setOnPreferenceChangeListener(this);

        mBackupModPreference.setOnPreferenceChangeListener(this);
        mRestoreModPreference.setOnPreferenceChangeListener(this);

        if (mRestorePreference != null) {
            mRestorePreference.setOnPreferenceChangeListener(this);
        }
        File firstKernel = new File("/storage/sdcard0/Frostbar/firstboot.img");

        if (!firstKernel.exists()) {

            SharedPreferences.Editor editor = wwwpref.edit();
            Intent intent = new Intent(this, DeviceSelectionActivity.class);
            startActivity(intent);

            deviceName = wwwpref.getString("device", "");
            deviceSetup = wwwpref.getBoolean("setup_done", false);
            if(deviceSetup) {

                if(deviceName.matches("")){
                    Toast.makeText(getBaseContext(), "Select device first", Toast.LENGTH_LONG).show();
                } else if(deviceName.equals(grand)) {
                     firstbackup = GalaxyGrandBlocks.backupFirstKernel();
                }
                else {

                     firstbackup = XperiaSBlocks.backupFirstKernel();
                }
                if (firstbackup) {

                    updateRestoreList();
                    Toast.makeText(getBaseContext(), "First Time Running app so the currently running kernel is backed up for safety", Toast.LENGTH_LONG).show();
                }
            }

        }

        if (mRecoveryPreference != null) {
            mRecoveryPreference.setOnPreferenceChangeListener(this);
        }
        updateRecoveryList();
        updateRestoreWithModuleList();

        if (mBackupRecoveryPreference != null) {
            mBackupRecoveryPreference.setOnPreferenceChangeListener(this);
        }


        if (prefScreen != null) {
            mMsimSwitchPreference = (SwitchPreference) prefScreen.findPreference(MSIM_PREF);
        }
        if (mMsimSwitchPreference != null) {
            mMsimSwitchPreference.setOnPreferenceChangeListener(this);
        }
        if (currentMsimStatus()) {

            mMsimSwitchPreference.setChecked(true);

        } else {

            mMsimSwitchPreference.setChecked(false);

        }

        if (prefScreen != null) {
            mDeletePreference = prefScreen.findPreference(DELETE_PREF);
        }
        mDeletePreference.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        if(preference == mDeletePreference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete Backups ?")
                        .setMessage("Are you sure you want to delete backups ?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                File dir = new File("/storage/sdcard0/Frostbar");
                                if (dir.exists()) {
                                    String[] entries = dir.list();
                                    for (String s : entries) {
                                        File file = new File(dir.getPath(), s);
                                        file.delete();
                                    }
                                    createDirectory();
                                    updateRecoveryList();
                                    updateRestoreList();

                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
            builder.create();
            builder.show();
        }

        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateRestoreList();
        updateRecoveryList();
        updateRestoreWithModuleList();


    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(rebootDialog_title)
                .setMessage(rebootDialog_message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        GeneralPurpose.normalShell("su -c reboot");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog rebootDialog = builder.create();


        if (preference == mMsimSwitchPreference) {

            boolean value = (Boolean) newValue;

            if (value) {

                enableMsim();
                rebootDialog.show();

            } else {

                disableMsim();
                rebootDialog.show();

            }


        } else if (preference == mBackupPreference) {

            String value = (String) newValue;
            if(deviceName.equals(grand)) {
                GalaxyGrandBlocks.BootBackup(value);
                Toast.makeText(getBaseContext(), "Grand : Backup of " + value + ".img is done", Toast.LENGTH_SHORT).show();
            } else if(deviceName.equals(xperiaS)) {
                XperiaSBlocks.BootBackup(value);
                Toast.makeText(getBaseContext(), "Xperia S : Backup of " + value + ".img is done", Toast.LENGTH_SHORT).show();
            }
            updateRestoreList();
        } else if (preference == mBackupModPreference) {

            String value = (String) newValue;
            if(deviceName.equals(grand)) {
                GalaxyGrandBlocks.BootBackupWithModules(value);
                Toast.makeText(getBaseContext(), "Grand : Backup of " + value + ".img is done", Toast.LENGTH_SHORT).show();
            } else if(deviceName.equals(xperiaS)) {
                XperiaSBlocks.BootBackupWithModules(value);
                Toast.makeText(getBaseContext(), "Xperia S : Backup of " + value + ".img is done", Toast.LENGTH_SHORT).show();
            }
            updateRestoreWithModuleList();
        } else if (preference == mBackupRecoveryPreference) {

            String value = (String) newValue;
            if(deviceName.equals(grand)) {
                GalaxyGrandBlocks.RecoveryBackup(value);
                Toast.makeText(getBaseContext(), "Grand : Backup of " + value + ".img is done", Toast.LENGTH_SHORT).show();
            } else if(deviceName.equals(xperiaS)) {
                XperiaSBlocks.RecoveryBackup(value);
                Toast.makeText(getBaseContext(), "Xperia S : Backup of " + value + ".img is done", Toast.LENGTH_SHORT).show();
            }
            updateRecoveryList();
        } else if (preference == mRestorePreference) {

            String value = (String) newValue;
            if(deviceName.equals(grand)) {
               boolean restoreState = GalaxyGrandBlocks.RestoreBackup(value);
                if (restoreState) {
                    rebootDialog.show();
                    Toast.makeText(getBaseContext(), "Grand : Restore of " + value + ".img is done", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "An error occured did you give root permission ?", Toast.LENGTH_SHORT).show();
                }

            } else if(deviceName.equals(xperiaS)) {
                boolean restoreState = XperiaSBlocks.RestoreBackup(value);
                if (restoreState) {
                    rebootDialog.show();
                    Toast.makeText(getBaseContext(), "Xperia S : Restore of " + value + ".img is done", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "An error occured did you give root permission ?", Toast.LENGTH_SHORT).show();
                }

            }

        } else if (preference == mRestoreModPreference) {

            String value = (String) newValue;
            boolean restoreState = GalaxyGrandBlocks.RestoreBackupWithModules(value, getApplicationContext());
            if (restoreState) {
                rebootDialog.show();
                Toast.makeText(getBaseContext(), "Grand : Restore of " + value + ".img is done", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "An error occured did you give root permission ?", Toast.LENGTH_SHORT).show();
            }
        } else if (preference == mRecoveryPreference) {

            String value = (String) newValue;
            if(deviceName.equals(grand)) {
                boolean restoreState = GalaxyGrandBlocks.SwitchRecovery(value);
                if (restoreState) {
                    rebootDialog.show();
                    Toast.makeText(getBaseContext(), "Grand : Restore of " + value + ".img is done", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "An error occured did you give root permission ?", Toast.LENGTH_SHORT).show();
                }

            } else if(deviceName.equals(xperiaS)) {
                boolean restoreState = XperiaSBlocks.SwitchRecovery(value);
                if (restoreState) {
                    rebootDialog.show();
                    Toast.makeText(getBaseContext(), "Xperia S : Restore of " + value + ".img is done", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "An error occured did you give root permission ?", Toast.LENGTH_SHORT).show();
                }

            }

        }

        return true;
    }

    private void createDirectory() {

        File file = new File("/storage/sdcard0/Frostbar/recovery");
        if (file.exists()) {
            Log.d(TAG, "Directory exists");

        } else {
            file.mkdirs();
        }

    }




    private boolean currentMsimStatus() {
        String msimValue = "dsds";
        String msimStatus = GeneralPurpose.normalShell(cGetMsim);
        return msimStatus.equals(msimValue);

    }

    private void enableMsim() {

        boolean eCommand = GeneralPurpose.executeCommand(cSetDualMsim);


    }

    private void disableMsim() {

        boolean dCommand = GeneralPurpose.executeCommand(cSetSingleMsim);

    }

    private void updateRestoreList() {

        File file;
        List<String> myList;

        myList = new ArrayList<String>();
        file = new File("/storage/sdcard0/Frostbar");
        File list[] = file.listFiles();

        if (list != null) {
            for (File aList : list) {
                if (aList.getName().endsWith(".img")) {
                    myList.add(aList.getName());
                }
            }
        }
        String[] strings = new String[myList.size()];
        strings = myList.toArray(strings);
        mRestorePreference.setEntryValues(strings);
        mRestorePreference.setEntries(strings);


    }

    private void updateRestoreWithModuleList() {

        File file;
        List<String> myList;

        myList = new ArrayList<String>();
        file = new File("/storage/sdcard0/Frostbar");
        File list[] = file.listFiles();

        if (list != null) {
            for (File aList : list) {
                if (aList.isDirectory()) {
                    myList.add(aList.getName());
                    if(myList.contains("recovery")){

                        myList.remove("recovery");
                    }
                }
            }
        }
        String[] strings = new String[myList.size()];
        strings = myList.toArray(strings);
        mRestoreModPreference.setEntryValues(strings);
        mRestoreModPreference.setEntries(strings);


    }

    private void updateRecoveryList() {

        File file;
        List<String> myList;

        myList = new ArrayList<String>();
        file = new File("/storage/sdcard0/Frostbar/recovery");
        File list[] = file.listFiles();

        if (list != null) {
            for (File aList : list) {
                myList.add(aList.getName());
            }
        }
        String[] strings = new String[myList.size()];
        strings = myList.toArray(strings);
        mRecoveryPreference.setEntryValues(strings);
        mRecoveryPreference.setEntries(strings);


    }

    public void setDevicePref(int id) {

        SharedPreferences.Editor editor = wwwpref.edit();

        if(id == 1) {

            editor.putString("GGD", grand);


        } else if(id == 2) {
            editor.putString("XS", xperiaS);

        }
        editor.apply();

    }



}
