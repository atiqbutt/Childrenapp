package com.softvilla.childapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import java.util.List;

/**
 * Created by Hassan on 1/1/2018.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    SharedPreferences preferences;
    @Override
    public void onReceive(Context context, Intent intent) {

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(haveNetworkConnection(context)){
            List<SynchApps> synchApps = SynchApps.listAll(SynchApps.class);
            for(SynchApps obj : synchApps){
                if(obj.isInstall){
                    updateInstallApp(obj.appName, obj.packageName, obj.icon,obj);
                }
                else {
                    updateUnInstallApp(obj.packageName,obj);
                }
            }
        }

    }


    public void updateInstallApp(String appName, String packageName, String icon, final SynchApps obj){

        AndroidNetworking.post("")
                .addBodyParameter("appName",appName)
                .addBodyParameter("pkgName", packageName)
                .addBodyParameter("id", preferences.getString("userId", ""))
                .addBodyParameter("img", icon)
                //.addBodyParameter("child_id", id)

                //.addBodyParameter("phone", Phno)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        obj.delete();

                    }


                    @Override
                    public void onError(ANError error) {

                    }
                });

    }

    public void updateUnInstallApp(String packageName, final SynchApps obj){

        AndroidNetworking.post("")
                //.addBodyParameter("appName",appName)
                .addBodyParameter("pkgName", packageName)
                .addBodyParameter("id", preferences.getString("userId", ""))
                //.addBodyParameter("img", getStringImage(bitmap))
                //.addBodyParameter("child_id", id)

                //.addBodyParameter("phone", Phno)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        obj.delete();
                    }


                    @Override
                    public void onError(ANError error) {

                    }
                });

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
