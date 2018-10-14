package fr.coding.tools.networks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class NetworkChangeReceiver extends BroadcastReceiver {

    public NetworkChangeEvent eventReceiver;

    private static final String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "event "+intent.getAction());

        if (eventReceiver != null)
            eventReceiver.networkChangeEvent(intent);
    }
}
