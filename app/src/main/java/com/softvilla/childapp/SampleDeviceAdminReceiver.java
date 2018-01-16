package com.softvilla.childapp;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Malik on 20/12/2017.
 */

public class SampleDeviceAdminReceiver extends DeviceAdminReceiver {



    @Override
    public void onDisabled(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Toast.makeText(context, "disabled dpm", Toast.LENGTH_SHORT).show();
        super.onDisabled(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {

        // TODO Auto-generated method stub
        Toast.makeText(context, "enabled dpm", Toast.LENGTH_SHORT).show();
        super.onEnabled(context, intent);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        // TODO Auto-generated method stub
        /*Toast.makeText(context, "disable dpm request", Toast.LENGTH_SHORT).show();
        return super.onDisableRequested(context, intent);*/
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain); //switch to the home screen, not totally necessary
       // lockPhone(context, secPassword);
        //Log.i(TAG, "DEVICE ADMINISTRATION DISABLE REQUESTED & LOCKED PHONE");

        //return "haha. i locked your phone.";
        return super.onDisableRequested(context, intent);
    }
   /* public static boolean lockPhone(Context context, String password){
        devAdminReceiver = new ComponentName(context, SampleDeviceAdminReceiver.class);
        dpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        boolean pwChange = dpm.resetPassword(password, 0);
        dpm.lockNow();
        return pwChange;
    }*/
}
