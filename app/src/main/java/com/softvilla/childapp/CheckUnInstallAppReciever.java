package com.softvilla.childapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

/**
 * Created by Malik on 29/12/2017.
 */

public class CheckUnInstallAppReciever extends BroadcastReceiver {


    SharedPreferences preferences;
    String packageName = null;
    String appName = null;
    @Override
    public void onReceive(Context context, Intent intent) {


        packageName = intent.getData().getEncodedSchemeSpecificPart();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        appName = null;
        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
       // Toast.makeText(context, "UnInstall App\n"+appName+"\n"+packageName, Toast.LENGTH_SHORT).show();

       /* IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addDataScheme("package");
        registerReceiver(br, intentFilter);*/

       if(haveNetworkConnection(context)){
           updateAppInfo();
       }

       else {
           SynchApps synchApps = new SynchApps();
           synchApps.packageName = packageName;
           synchApps.isInstall = false;
           synchApps.save();
       }
    }

    public void updateAppInfo(){

        AndroidNetworking.post("http://noorpublicschool.com/ApiPractice/updateAppInfo")
                //.addBodyParameter("appName",appName)
                .addBodyParameter("pkgName", packageName)
                .addBodyParameter("chidId", preferences.getString("child_id", ""))
                //.addBodyParameter("img", getStringImage(bitmap))
                //.addBodyParameter("child_id", id)

                //.addBodyParameter("phone", Phno)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {


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
