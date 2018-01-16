package com.softvilla.childapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Malik on 8/23/2017.
 */

public class LaunchAppViaDialReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String number = getResultData();

        if (number == null) {
            // No reformatted number, use the original
            number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }

        //Toast.makeText(context, number, Toast.LENGTH_SHORT).show();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if(number.equalsIgnoreCase("#" + preferences.getString("pin",""))){

            setResultData(null);
            /*PackageManager p = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, com.zh.applock.MainActivity.class);
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);*/

            context.startActivity(new Intent(context,QRCode.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            /*Intent i=new Intent(context,NavigationDrawer.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);*/
        }
    }
 }
