package com.ashish.frostbar.helper;

import android.content.Context;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Hp on 24-03-2015.
 */
public class XperiaSBlocks {

    public static boolean backupFirstKernel() {


        return GeneralPurpose.executeCommand("dd if=/dev/block/mmcblk0p3 of=/sdcard/Frostbar/firstboot.img");

    }

    public static void BootBackup(String value) {

        GeneralPurpose.executeCommand("dd if=/dev/block/mmcblk0p3 of=/sdcard/Frostbar/" + value + ".img");
    }

    public static void BootBackupWithModules(String value) {

        File file = new File("/sdcard/Frostbar/" + value);
        if (file.exists()) {

            file.delete();

        } else {

            file.mkdirs();

        }
        File modules = new File("/sdcard/Frostbar/" + value + "/modules");

        GeneralPurpose.executeCommand("dd if=/dev/block/mmcblk0p3 of=/sdcard/Frostbar/" + value + "/" + value + ".img");
        GeneralPurpose.executeCommand("cp /system/lib/modules/* /sdcard/Frostbar/" + value + "/modules");
    }


    public static boolean RestoreBackup(String value) {


        return GeneralPurpose.executeCommand("dd if=/sdcard/Frostbar/" + value + " of=/dev/block/mmcblk0p3");

    }

    public static boolean RestoreBackupWithModules(String value, Context context) {

        File file = new File("/sdcard/Frostbar/" + value);
        if (file.exists()) {

            File modules = new File("/sdcard/Frostbar/" + value + "/modules");
            if(!modules.exists()) {

                modules.mkdirs();

            }

            GeneralPurpose.executeCommand("dd if=/sdcard/Frostbar/" + value + "/" + value + ".img of=/dev/block/mmcblk0p3");
            return GeneralPurpose.executeCommand("cp /sdcard/Frostbar/" + value + "/modules /system/lib/modules");

        } else {

            Toast.makeText(context, "Backup does not exists", Toast.LENGTH_SHORT).show();
            return false;

        }

    }

    public static void RecoveryBackup(String value) {

        GeneralPurpose.executeCommand("dd if=/dev/block/mmcblk0p11 of=/sdcard/Frostbar/recovery/" + value + ".img");
    }

    public static boolean SwitchRecovery(String value) {


        return GeneralPurpose.executeCommand("dd if=/sdcard/Frostbar/recovery/" + value + " of=/dev/block/mmcblk0p11");

    }
}
