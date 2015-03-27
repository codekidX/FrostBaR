package com.ashish.frostbar.helper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Ashish on 24-03-2015.
 */
public class GeneralPurpose {

    public static final String TAG = "General Purpose";

    public static String normalShell(String normalCommand) {

        String rOutput = null;
        try {
            Process mProcess = Runtime.getRuntime().exec(normalCommand);
            mProcess.waitFor();
            BufferedReader mReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            rOutput = mReader.readLine();
            Log.i(TAG, "Output from normalShell = " + rOutput);
        } catch (IOException e) {
            Log.e(TAG, "CommandShell: Unable to execute command at shell without root - I/O Exception");
        } catch (InterruptedException e) {
            Log.e(TAG, "CommandShell: Unable to execute command at shell without root - Process interrupted");
        }

        return rOutput;


    }

    public static boolean executeCommand(String command) {

        try {
            Process getRootAccess = Runtime.getRuntime().exec("su");
            DataOutputStream getRootAccessDos = new DataOutputStream(getRootAccess.getOutputStream());
            getRootAccessDos.writeBytes(command + "\n");
            getRootAccessDos.writeBytes("exit\n");
            getRootAccessDos.flush();
            getRootAccess.waitFor();

            if (getRootAccess.exitValue() == 1) {

                Log.e(TAG, "CommandShell: SuperUser Access demied.");
                return false;

            } else {


                Log.i(TAG, "CommandShell: SuperUser permission granted.");
                return true;
            }

        } catch (IOException e) {

            Log.e(TAG, "CommandShell: Unable to execute command as superuser!");
            return false;

        } catch (InterruptedException e) {

            Log.e(TAG, "CommandShell: executeCommand method interrupted");
            return false;

        }


    }

}
