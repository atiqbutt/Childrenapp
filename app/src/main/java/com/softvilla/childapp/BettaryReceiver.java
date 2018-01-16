package com.softvilla.childapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.preference.PreferenceManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Malik on 28/12/2017.
 */

public class BettaryReceiver extends BroadcastReceiver {

    int scale;
    int level;
    float percentage;
    Calendar calander;
    SimpleDateFormat simpledateformat;
    String Date;
    SharedPreferences preferences;


    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Receiver", Toast.LENGTH_SHORT).show();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        AndroidNetworking.initialize(context);
            /*
                BatteryManager
                    The BatteryManager class contains strings and constants used for values in the
                    ACTION_BATTERY_CHANGED Intent, and provides a method for querying battery
                    and charging properties.
            */
            /*
                public static final String EXTRA_SCALE
                    Extra for ACTION_BATTERY_CHANGED: integer containing the maximum battery level.
                    Constant Value: "scale"
            */
        // Get the battery scale
        scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        // Display the battery scale in TextView
        // mTextViewInfo.setText("Battery Scale : " + scale);

            /*
                public static final String EXTRA_LEVEL
                    Extra for ACTION_BATTERY_CHANGED: integer field containing the current battery
                    level, from 0 to EXTRA_SCALE.

                    Constant Value: "level"
            */
        // get the battery level
        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        // Display the battery level in TextView
        // mTextViewInfo.setText(mTextViewInfo.getText() + "\nBattery Level : " + level);

        // Calculate the battery charged percentage
        percentage = level / (float) scale;
        // Update the progress bar to display current battery charged percentage
        // mProgressStatus = (int)((percentage)*100);

        calander = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date = simpledateformat.format(calander.getTime());

        if (haveNetworkConnection(context)) {
            saveBatteryStatus();
        }

        // Show the battery charged percentage text inside progress bar
        // mTextViewPercentage.setText("" + mProgressStatus + "%");

        // Show the battery charged percentage in TextView
        // mTextViewInfo.setText(mTextViewInfo.getText() +"\nPercentage : "+ mProgressStatus + "%");

        // Display the battery charged percentage in progress bar
        // mProgressBar.setProgress(mProgressStatus);
    }

    public void saveBatteryStatus() {
        AndroidNetworking.post("http://noorpublicschool.com/ApiPractice/UpdateLocation")
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
                        // do anything with response
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
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