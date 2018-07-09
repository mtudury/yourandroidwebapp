package fr.coding.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Perms {

    public static boolean checkWriteSDPermission(Context context) {
        return (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }


    public static void requestWriteSDPermissio(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                requestCode);
    }

    public static boolean checkReadSDPermission(Context context) {
        return (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }


    public static void requestReadSDPermissio(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                requestCode);
    }

    public static boolean checkGetAccountsPermission(Context context) {
        return (ContextCompat.checkSelfPermission(context,
                Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED);
    }


    public static void requestGetAccountsPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.GET_ACCOUNTS},
                requestCode);
    }
}
