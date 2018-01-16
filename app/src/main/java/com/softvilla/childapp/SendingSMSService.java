package com.softvilla.childapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import java.util.Date;
import java.util.List;

/**
 * Created by Hassan on 11/30/2017.
 */

public class SendingSMSService extends Service {

    String SENDING_SMS_URL = "https://teensafe.000webhostapp.com/Api/uploadSms";
    SharedPreferences preferences;
    List<SmsTable>  messages;
    Handler handler;
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
        messages = SmsTable.listAll(SmsTable.class);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        AndroidNetworking.initialize(SendingSMSService.this);



        sendSms(0);

        super.onCreate();
    }


    public void sendSms(final int count){
        final SmsTable obj = messages.get(count);
        final Utilities utilities = Utilities.findById(Utilities.class, (long) 1);
        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(haveNetworkConnection()){
                    if(count == 4000){
                        utilities.lastSmsSendTime = new Date().getTime();
                        stopService(new Intent(SendingSMSService.this,SendingSMSService.class));

                    }
                    else  if(!obj.isSend){
                        AndroidNetworking.post(SENDING_SMS_URL)
                                .addBodyParameter("pnoneNumber", obj.phoneNumber)
                                .addBodyParameter("messageDate", obj.messageDate)
                                .addBodyParameter("messageType", obj.messageType)
                                .addBodyParameter("messageBody", obj.messageBody)
                                .addBodyParameter("name", obj.name)
                                .addBodyParameter("chidId", preferences.getString("userId",""))
                                .setTag("test")
                                .setPriority(Priority.HIGH)
                                .build()
                                .getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        obj.isSend = true;
                                        obj.save();

                                        if(count + 1 == messages.size()){
                                            stopService(new Intent(SendingSMSService.this,SendingSMSService.class));
                                        }
                                        else {
                                            sendSms(count + 1);
                                        }
                                        // do anything with response
                                    }
                                    @Override
                                    public void onError(ANError error) {
                                        if(count + 1 == messages.size()){
                                            stopService(new Intent(SendingSMSService.this,SendingSMSService.class));
                                        }
                                        else {
                                            sendSms(count + 1);
                                        }
                                        // handle error
                                    }
                                });

                    }
                    else {
                        if(count + 1 == messages.size()){
                            stopService(new Intent(SendingSMSService.this,SendingSMSService.class));
                        }
                        else {
                            sendSms(count + 1);
                        }
                    }
                }
                else {

                    utilities.lastSmsSendTime = new Date().getTime();
                    stopService(new Intent(SendingSMSService.this,SendingSMSService.class));
                }
            }
        }, 100);



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
