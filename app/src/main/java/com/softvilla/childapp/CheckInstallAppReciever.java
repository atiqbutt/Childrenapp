package com.softvilla.childapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by Malik on 29/12/2017.
 */

public class CheckInstallAppReciever extends BroadcastReceiver {

    SharedPreferences preferences;
    String packageName = null;
    String appName = null;
    Drawable ico = null;
    Bitmap bitmap = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        AndroidNetworking.initialize(context);


        packageName = intent.getData().getEncodedSchemeSpecificPart();
        PackageManager packageManager = context.getApplicationContext().getPackageManager();


        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));


            List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

            for(ApplicationInfo applicationInfo : applicationInfos){
                if(applicationInfo.packageName.equalsIgnoreCase(packageName)){
                   ico = applicationInfo.loadIcon(packageManager);
                    bitmap = ((BitmapDrawable)ico).getBitmap();

                }
            }
           /* Intent intent1 = new Intent(context,AppInfo.class);
            intent1.putExtra("appname",appName);
            intent1.putExtra("apppkg",packageName);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent1.putExtra("img",image);
            context.startActivity(intent1);*/


        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


       // Toast.makeText(context, "Install New App\n"+appName+"\n"+packageName, Toast.LENGTH_SHORT).show();

        if (haveNetworkConnection(context)){
            uploadAppInfo();
        }

        else {
            SynchApps synchApps = new SynchApps();
            synchApps.packageName = packageName;
            synchApps.appName = appName;
            synchApps.icon = getStringImage(bitmap);
            synchApps.isInstall = true;
            synchApps.save();
        }

       /* IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addDataScheme("package");
        registerReceiver(br, intentFilter);*/

    }

    public void uploadAppInfo(){

        AndroidNetworking.post("http://noorpublicschool.com/ApiPractice/uploadAppInfo")
                .addBodyParameter("appName",appName)
                .addBodyParameter("pkgName", packageName)
                .addBodyParameter("chidId", preferences.getString("child_id", ""))
                .addBodyParameter("img", getStringImage(bitmap))
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
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
