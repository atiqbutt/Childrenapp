package com.softvilla.childapp;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by Malik on 11/29/2017.
 */

public class SavingCallLogsService extends Service {
    Handler handler;

    SharedPreferences preferences;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onCreate() {

        AndroidNetworking.initialize(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                    Utilities obj = Utilities.findById(Utilities.class, (long) 1);
                    if (ActivityCompat.checkSelfPermission(SavingCallLogsService.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                Uri allCalls = Uri.parse("content://call_log/calls");
                    Cursor managedCursor = getContentResolver().query(allCalls, null,"_id >" + obj.LATEST_CALL_LOG_ID, null, "_id ASC");
                    int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                    int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                    int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                    int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                    int id = managedCursor.getColumnIndex(CallLog.Calls._ID);

                if (managedCursor.moveToFirst()) {
                    for (int i = 0; i < managedCursor.getCount(); i++) {
                        Utilities utilities = Utilities.findById(Utilities.class, (long) 1);
                        String phNumber = managedCursor.getString(number);
                        String callType = managedCursor.getString(type);
                        String callDate = managedCursor.getString(date);
                        Date callDayTime = new Date(Long.valueOf(callDate));
                        String callDuration = managedCursor.getString(duration);
                        String callId = managedCursor.getString(id);
                        String dir = null;
                        String name = getContactName(SavingCallLogsService.this, phNumber);
                        int dircode = Integer.parseInt(callType);
                        switch (dircode) {
                            case CallLog.Calls.OUTGOING_TYPE:
                                dir = "OUTGOING";
                                break;

                            case CallLog.Calls.INCOMING_TYPE:
                                dir = "INCOMING";
                                break;

                            case CallLog.Calls.MISSED_TYPE:
                                dir = "MISSED";
                                break;
                        }

                        if (utilities.LATEST_CALL_LOG_ID < Long.parseLong(callId)) {
                            utilities.LATEST_CALL_LOG_ID = Long.parseLong(callId);
                            utilities.save();
                            if (name == null) {
                                name = "No Name";
                            }
                            CallLogDetail callLogDetail = new CallLogDetail();

                            callLogDetail.phoneNumber = phNumber;
                            callLogDetail.name = name;
                            callLogDetail.callDate = callDayTime.toString();
                            callLogDetail.callDuration = callDuration;
                            callLogDetail.callType = dir;

                            callLogDetail.save();
                            //Toast.makeText(SavingCallLogsService.this,managedCursor.getCount(), Toast.LENGTH_SHORT).show();
                        }
                        managedCursor.moveToNext();
                    }
                    }
                    managedCursor.close();

                    final List<CallLogDetail> callLogs = CallLogDetail.findWithQuery(CallLogDetail.class,"Select * from Call_Log_Detail limit 500");//CallLogDetail.listAll(CallLogDetail.class);

                    JSONArray calllog = new JSONArray();

                    for(CallLogDetail callLogDetail : callLogs){

                        JSONObject jsonObject = new JSONObject();

                        try {
                            if(callLogDetail.phoneNumber == null){
                                jsonObject.put("phone_number","Test");
                            }
                            else {
                                jsonObject.put("phone_number",callLogDetail.phoneNumber);
                            }

                            if(callLogDetail.callType == null){
                                jsonObject.put("call_type","test");
                            }
                            else {
                                jsonObject.put("call_type",callLogDetail.callType);
                            }

                            if(callLogDetail.callDate == null){
                                jsonObject.put("call_date","test");
                            }
                            else {
                                jsonObject.put("call_date",callLogDetail.callDate);
                            }if(callLogDetail.callDuration == null){
                                jsonObject.put("call_duration","test");
                            }
                            else {
                                jsonObject.put("call_duration",callLogDetail.callDuration);
                            }
                            if(callLogDetail.name == null){
                                jsonObject.put("name","test");
                            }
                            else {
                                jsonObject.put("name",callLogDetail.name);
                            }
                            if(preferences.getString("child_id","").equalsIgnoreCase("")){
                                jsonObject.put("child_id","test");
                            }
                            else {
                                jsonObject.put("child_id",preferences.getString("child_id",""));
                            }

                        calllog.put(jsonObject);
                        } catch (JSONException e) {
                           // Toast.makeText(SavingCallLogsService.this, e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }


                    if(callLogs.size() > 0){
                       // Toast.makeText(SavingCallLogsService.this, "Calling Greter then 0", Toast.LENGTH_SHORT).show();
                        sendCallLogs(calllog,callLogs);
                    }
                    else {
                        //Toast.makeText(SavingCallLogsService.this, "Calling less then 0", Toast.LENGTH_SHORT).show();
                        stopService(new Intent(SavingCallLogsService.this,SavingCallLogsService.class));
                    }


                    /*AndroidNetworking.post("https://teensafe.000webhostapp.com/Api/uploadCallLog2")
                            .addBodyParameter("calllog",calllog.toString())// posting json
                            .setTag("test")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {

                                    // do anything with response
                                    if(response.equalsIgnoreCase("success")){
                                        CallLogDetail.deleteAll(CallLogDetail.class);
                                        List<CallLogDetail> list = CallLogDetail.findWithQuery(CallLogDetail.class,"Select * FROM Call_Log_Detail limit 1000");
                                        if(list.size() > 0){
                                            JSONArray calllog = new JSONArray();

                                            for(CallLogDetail callLogDetail : callLogs){

                                                JSONObject jsonObject = new JSONObject();

                                                try {
                                                    jsonObject.put("phone_number",callLogDetail.phoneNumber);
                                                    jsonObject.put("call_date",callLogDetail.callDate);
                                                    jsonObject.put("call_duration",callLogDetail.callDuration);
                                                    jsonObject.put("call_type",callLogDetail.callType);
                                                    jsonObject.put("name",callLogDetail.name);
                                                    jsonObject.put("child_id",preferences.getString("userId",""));

                                                    calllog.put(jsonObject);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            sendCallLogs(calllog,list);
                                        }
                                        else {
                                            stopService(new Intent(SavingCallLogsService.this,SavingCallLogsService.class));
                                        }

                                    }
                                    else {
                                        stopService(new Intent(SavingCallLogsService.this,SavingCallLogsService.class));
                                    }


                                }
                                @Override
                                public void onError(ANError error) {
                                    // handle error
                                    //Toast.makeText(SavingCallLogsService.this, error.toString(), Toast.LENGTH_LONG).show();

                                }
                            });*/



            }
        }, 300);
        }
        catch (Exception e){

            //Toast.makeText(SavingCallLogsService.this, e.toString(), Toast.LENGTH_LONG).show();
        }
        super.onCreate();
    }


    public void sendCallLogs(final JSONArray calllog, final List<CallLogDetail> callLogDetailList){
        Handler mHandler = new Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
        AndroidNetworking.post("http://noorpublicschool.com/ApiPractice/uploadCallLog2")
                .addBodyParameter("calllog",calllog.toString())// posting json
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                       // Toast.makeText(SavingCallLogsService.this, response, Toast.LENGTH_LONG).show();

                        // do anything with response
                        if(response.equalsIgnoreCase("success")){
                            for(CallLogDetail callLogDetail : callLogDetailList){
                                callLogDetail.delete();
                            }
                            List<CallLogDetail> list = CallLogDetail.findWithQuery(CallLogDetail.class,"Select * from Call_Log_Detail limit 500");
                            if(list.size() > 0){
                                JSONArray jsonArray = new JSONArray();

                                for(CallLogDetail callLogDetail : list){

                                    JSONObject jsonObject = new JSONObject();

                                    try {
                                        jsonObject.put("phone_number",callLogDetail.phoneNumber);
                                        jsonObject.put("call_date",callLogDetail.callDate);
                                        jsonObject.put("call_duration",callLogDetail.callDuration);
                                        jsonObject.put("call_type",callLogDetail.callType);
                                        jsonObject.put("name",callLogDetail.name);
                                        jsonObject.put("child_id",preferences.getString("child_id",""));

                                        jsonArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                sendCallLogs(jsonArray,list);
                            }
                            else {
                                stopService(new Intent(SavingCallLogsService.this,SavingCallLogsService.class));
                            }

                        }
                        else {
                            sendCallLogs(calllog,callLogDetailList);
                        }


                    }
                    @Override
                    public void onError(ANError error) {
                      //  Toast.makeText(SavingCallLogsService.this, error.toString(), Toast.LENGTH_SHORT).show();
                        if(haveNetworkConnection()){
                            sendCallLogs(calllog,callLogDetailList);
                        }
                        else {
                            stopService(new Intent(SavingCallLogsService.this,SavingCallLogsService.class));
                        }

                        // handle error
                        //Toast.makeText(SavingCallLogsService.this, error.toString(), Toast.LENGTH_LONG).show();

                    }
                });
    }
}, 300);

    }


    public String getContactName(Context context, String phoneNumber) {
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(phoneNumber));
            Cursor cursor = cr.query(uri,
                    new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
            if (cursor == null) {
                return null;
            }
            String contactName = null;
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return contactName;
        }
        catch (Exception e){
            return "No Name";
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
