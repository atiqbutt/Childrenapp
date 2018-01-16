package com.softvilla.childapp;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.rvalerio.fgchecker.AppChecker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Malik on 7/5/2017.
 */

public class TrackingService extends Service {
    private static final String TAG = "TEEN_SAVE_SERVICE";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 2000;
    private static final float LOCATION_DISTANCE = 100f;
    SharedPreferences preferences;
    Handler mHandler;
    int scale;
    int level;
    float percentage;
    Calendar calander;
    SimpleDateFormat simpledateformat;
    String Date;
    boolean isLock, isOld;
    String currentOpenAppPackageName;
    AppChecker appChecker;



    /*private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Receiver", Toast.LENGTH_SHORT).show();
            *//*
                BatteryManager
                    The BatteryManager class contains strings and constants used for values in the
                    ACTION_BATTERY_CHANGED Intent, and provides a method for querying battery
                    and charging properties.
            *//*
            *//*
                public static final String EXTRA_SCALE
                    Extra for ACTION_BATTERY_CHANGED: integer containing the maximum battery level.
                    Constant Value: "scale"
            *//*
            // Get the battery scale
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
            // Display the battery scale in TextView
           // mTextViewInfo.setText("Battery Scale : " + scale);

            *//*
                public static final String EXTRA_LEVEL
                    Extra for ACTION_BATTERY_CHANGED: integer field containing the current battery
                    level, from 0 to EXTRA_SCALE.

                    Constant Value: "level"
            *//*
            // get the battery level
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            // Display the battery level in TextView
           // mTextViewInfo.setText(mTextViewInfo.getText() + "\nBattery Level : " + level);

            // Calculate the battery charged percentage
            percentage = level/ (float) scale;
            // Update the progress bar to display current battery charged percentage
           // mProgressStatus = (int)((percentage)*100);

            calander = Calendar.getInstance();
            simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date = simpledateformat.format(calander.getTime());

            if(haveNetworkConnection()){
                saveBatteryStatus();
            }

            // Show the battery charged percentage text inside progress bar
           // mTextViewPercentage.setText("" + mProgressStatus + "%");

            // Show the battery charged percentage in TextView
           // mTextViewInfo.setText(mTextViewInfo.getText() +"\nPercentage : "+ mProgressStatus + "%");

            // Display the battery charged percentage in progress bar
           // mProgressBar.setProgress(mProgressStatus);
        }
    };*/





    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            //Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            if(haveNetworkConnection()){
                AndroidNetworking.post("http://noorpublicschool.com/ApiPractice/UpdateLocation")
                        .addBodyParameter("coordinates", String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude()))
                        //.addBodyParameter("lng",  String.valueOf(location.getLongitude()))
                        .addBodyParameter("child_id", preferences.getString("child_id",""))
                        .setTag("test")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                // do anything with response
                            }
                            @Override
                            public void onError(ANError error) {
                                // handle error
                            }
                        });
            }

            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            //Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            //Log.e(TAG, "onProviderEnabled: " + provider);
           // Toast.makeText(TrackingService.this,provider, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            //Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };



    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        appChecker = new AppChecker();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        AndroidNetworking.initialize(this);
        /*IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(mBroadcastReceiver,iFilter);*/
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentFilter iF = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent intent = TrackingService.this.registerReceiver(null,iF);
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                percentage = level / (float) scale;
                calander = Calendar.getInstance();
                simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date = simpledateformat.format(calander.getTime());

                if (haveNetworkConnection()) {
                    saveBatteryStatus();
                }

                isOld = false;
                List<LockedApp> lockedApps = LockedApp.listAll(LockedApp.class);

                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                    ActivityManager am = (ActivityManager) TrackingService.this.getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
                    for(LockedApp lockedApp : lockedApps){
                        if(foregroundTaskInfo .topActivity.getPackageName().equalsIgnoreCase(lockedApp.packageName)){
                            isLock = true;
                            currentOpenAppPackageName = lockedApp.packageName;
                        }
                    }
                    if(isLock){
                        isOld = true;
                        isLock = false;
                        Intent intent1;// = new Intent(MonitoringService.this,ApplyPattern.class);
                        intent1 = new Intent(TrackingService.this,ErrorDialog.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.putExtra("packageName",currentOpenAppPackageName);
                        startActivity(intent1);
                    }

                    while (isOld){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(foregroundTaskInfo .topActivity.getPackageName().equalsIgnoreCase(currentOpenAppPackageName) || foregroundTaskInfo .topActivity.getPackageName().equalsIgnoreCase("com.softvilla.childapp")){

                        }
                        else {
                            isOld = false;
                        }
                    }
                }
                else {

                    for(LockedApp lockedApp : lockedApps){
                        if(appChecker.getForegroundApp(TrackingService.this).equalsIgnoreCase(lockedApp.packageName)){
                            isLock = true;
                            currentOpenAppPackageName = lockedApp.packageName;
                        }
                    }
                    if(isLock){
                        isOld = true;
                        isLock = false;
                        Intent intent2;// = ne1w Intent(MonitoringService.this,ApplyPattern.class);
                        intent2 = new Intent(TrackingService.this,ErrorDialog.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent2.putExtra("packageName",currentOpenAppPackageName);
                        startActivity(intent2);

                    }

                    while (isOld){
                        Utilities obj = Utilities.findById(Utilities.class, (long) 1);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(appChecker.getForegroundApp(TrackingService.this).equalsIgnoreCase(currentOpenAppPackageName) || appChecker.getForegroundApp(TrackingService.this).equalsIgnoreCase("com.zh.applock")){

                        }
                        else {
                            isOld = false;
                        }
                    }
                }

                mHandler.postDelayed(this,1000);
            }
        },1000);
        super.onCreate();
    }

    public void saveBatteryStatus() {

        AndroidNetworking.post("http://noorpublicschool.com/ApiPractice/UpdateBettaryStatus")
                .addBodyParameter("bettaryscale", String.valueOf(scale))
                .addBodyParameter("bettarylevel", String.valueOf(level))
                .addBodyParameter("bettarypercentage", String.valueOf(percentage))
                .addBodyParameter("date", Date)
                .addBodyParameter("child_id", preferences.getString("child_id", ""))
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                       // Toast.makeText(TrackingService.this, response, Toast.LENGTH_SHORT).show();
                        // do anything with response
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                       // Toast.makeText(TrackingService.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void initializeLocationManager() {
        //Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }



    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
