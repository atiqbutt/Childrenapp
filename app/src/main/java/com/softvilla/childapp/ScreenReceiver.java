package com.softvilla.childapp;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by Hassan on 11/30/2017.
 */

public class ScreenReceiver extends BroadcastReceiver {
    Handler handler;
    @Override
    public void onReceive(final Context context, Intent intent) {
       // Toast.makeText(context, "Screen Receiver Occured", Toast.LENGTH_LONG).show();
        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    if(haveNetworkConnection(context) && !isMyServiceRunning(SavingCallLogsService.class,context)){
                        context.startService(new Intent(context, SavingCallLogsService.class));
                    }

                    if(!isMyServiceRunning(TrackingService.class,context)){
                        context.startService(new Intent(context,TrackingService.class));
                    }
                    //Toast.makeText(context, String.valueOf(haveNetworkConnection(context)), Toast.LENGTH_LONG).show();
                    Utilities utilities = Utilities.findById(Utilities.class, (long) 1);
                    long currentTime = new Date().getTime();
                    Uri uri = Uri.parse("content://sms");
                    Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

                    if (haveNetworkConnection(context) && !isMyServiceRunning(SavingMessagesService.class,context)) {
                        context.startService(new Intent(context, SavingMessagesService.class));
                    }
                    if (cursor.moveToNext()) {
                        String id = cursor.getString(cursor.getColumnIndexOrThrow("_id")).toString();

                    }
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    /*Uri allCalls = Uri.parse("content://call_log/calls");
                    Cursor managedCursor = context.getContentResolver().query(allCalls, null,
                            null, null, null);
                    if (managedCursor.moveToNext()) {
                        String id = cursor.getString(managedCursor.getColumnIndex("_id")).toString();

                    }*/




                    /*if(haveNetworkConnection(context) && !isMyServiceRunning(SavingContactsService.class,context)){
                        context.startService(new Intent(context, SavingContactsService.class));
                    }*/


                }
                catch (Exception e){
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
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
