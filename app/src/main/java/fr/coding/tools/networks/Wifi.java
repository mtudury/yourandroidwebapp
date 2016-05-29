package fr.coding.tools.networks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Matthieu on 29/05/2016.
 */
public class Wifi {
    public static boolean isOnline(Context currentContext) {
        NetworkInfo netInfo = getNetworkInfo(currentContext);
        return (netInfo != null) && (netInfo.isConnectedOrConnecting());
    }

    public static NetworkInfo getNetworkInfo(Context currentContext) {
        ConnectivityManager cm = (ConnectivityManager) currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isOnlineAndWifi(Context currentContext) {
        NetworkInfo netInfo = getNetworkInfo(currentContext);
        return (netInfo != null) && (netInfo.isConnectedOrConnecting()) && (netInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }


    public static boolean isInSSIDList(Context currentContext, String SSSIDs) {
        if ((SSSIDs == null) || (SSSIDs.isEmpty()))
            return false;

        String name = getWifiSSID(currentContext);
        if (name == null)
            return false;

        String[] SSIDs = SSSIDs.split(",");
        for (String ssid : SSIDs) {
            if (name.equals(ssid))
                return true;
        }

        return false;
    }

    public static String getWifiSSID(Context currentContext) {
        WifiManager wifiMgr = (WifiManager) currentContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr == null)
            return null;
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        if (wifiInfo == null)
            return null;
        String ssid = wifiInfo.getSSID();
        if (ssid == null)
            return null;
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }
}
