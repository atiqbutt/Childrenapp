package com.softvilla.childapp;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.format.Time;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static com.softvilla.childapp.MainActivity.TAG;

/**
 * Created by Malik 11/30/2017.
 */

public class CheckForeGroundAppService extends Service {

    Handler handler;
    SharedPreferences preferences;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return  START_STICKY;
    }

    @Override
    public void onCreate() {

        AndroidNetworking.initialize(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try{

            handler = new Handler();

            handler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {

                    printForegroundTask();
                    handler.postDelayed(this,1000);
                }
            }, 1000);
        }catch (Exception e){
            //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        super.onCreate();
    }


    private void printForegroundTask() {
        String currentApp = "NULL";
        long time1 = System.currentTimeMillis();
        long time = System.currentTimeMillis();
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        today.format("%k:%M:%S");
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);

            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                    mySortedMap.put(TimeUnit.MILLISECONDS.toMinutes(usageStats.getTotalTimeInForeground()), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    time1 = TimeUnit.MILLISECONDS.toMinutes(mySortedMap.get(mySortedMap.lastKey()).getTotalTimeInForeground());

                    AndroidNetworking.post("http://noorpublicschool.com/ApiPractice/UpdateAppUsageTime")
                            .addBodyParameter("pkgName",currentApp)
                            .addBodyParameter("time", String.valueOf(time1))
                            .addBodyParameter("chidId", preferences.getString("child_id", ""))
                           // .addBodyParameter("img", icon)
                            .setTag("test")
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
            }
        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
       // Toast.makeText(CheckForeGroundAppService.this, "Current App in foreground is: " + currentApp+time1, Toast.LENGTH_SHORT).show();

        //Log.e(TAG, "Current App in foreground is: " + currentApp);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("WrongConstant")
    public static String getForegroundAppPackageName(Context context) {
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService("usagestats");
        long time = System.currentTimeMillis();
        List<UsageStats> list = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
        if (list != null && !list.isEmpty()) {
            SortedMap<Long, UsageStats> map = new TreeMap<>();
            for (UsageStats stats : list) {
                map.put(stats.getLastTimeUsed(), stats);
            }
            if (!map.isEmpty()) {
                return map.get(map.lastKey()).getPackageName();
            }
        }
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void printUsageStats(List<UsageStats> usageStatsList) {
        for (UsageStats u : usageStatsList) {

            Log.d(TAG, "Pkg: " + u.getPackageName() + "\t"
                    + "ForegroundTime: " + u.getTotalTimeInForeground());
        }


    }


}
