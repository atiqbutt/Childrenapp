package com.softvilla.childapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test extends AppCompatActivity {

    TextView textView;
    ProgressDialog dialog;
    StringBuffer stringBuffer;
    String SENDING_SMS_URL = "https://teensafe.000webhostapp.com/Api/uploadCaalLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        stringBuffer = new StringBuffer();
        textView = (TextView) findViewById(R.id.bufferString);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Fetching Sms...");

        List<Utilities> utilities = Utilities.listAll(Utilities.class);

        if (utilities.size() == 0) {
            Utilities obj = new Utilities();
            obj.LATEST_CALL_LOG_ID = 0;
            obj.LATEST_SMS_ID = 0;
            obj.save();
        }

        getContacts(this);

        /*textView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startService(new Intent(Test.this,SavingCallLogsService.class));
                    }
                }
        );*/


        /*StringBuffer sb = new StringBuffer();
        int i = 1;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Utilities obj = Utilities.findById(Utilities.class, (long) 1);
        Uri uri = Uri.parse("content://call_log/calls");
        Cursor managedCursor = getContentResolver().query(uri, null,
                "_id >" + obj.LATEST_CALL_LOG_ID, null, "_id ASC");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
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
            sb.append("\n" +String.valueOf(i)+"\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callDayTime
                    + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");

            i++;
        }
        managedCursor.close();

        Toast.makeText(this, String.valueOf(i), Toast.LENGTH_SHORT).show();*/

        //TextView textView = (TextView) findViewById(R.id.calls);

        //textView.setText(sb);
        //getSMSDetails();
    }


    private void getSMSDetails() {
        new LongOperation().execute("");

    }


    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            stringBuffer.append("*********SMS History*************** :");
            Uri uri = Uri.parse("content://sms");
            Cursor cursor = getContentResolver().query(uri, null, null, null, "_id ASC");

            if (cursor.moveToFirst()) {
                for (int i = 0; i < 200; i++) {
                    String body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                            .toString();
                    String number = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                            .toString();
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                            .toString();
                    Date smsDayTime = new Date(Long.valueOf(date));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                            .toString();

                    String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"))
                            .toString();
                    String name = getContactName(Test.this,number);
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

                    stringBuffer.append("\nPhone Number:--- " + number + " \nMessage Type:--- "
                            + typeOfSMS + " \nMessage Date:--- " + smsDayTime
                            + " \nMessage Body:--- " + body+ " \nName:--- " + name
                            + " \nid:--- " + id);
                    stringBuffer.append("\n----------------------------------");
                    cursor.moveToNext();
                }

            }
            cursor.close();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            textView.setText(stringBuffer);
            // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public List<ContactModel> getContacts(Context ctx) {
        stringBuffer.append("*********SMS History*************** :");
        List<ContactModel> list = new ArrayList<>();
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                    Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                    Bitmap photo = null;
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                    }
                    while (cursorInfo.moveToNext()) {
                        ContactModel info = new ContactModel();

                        info.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        info.mobileNumber = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        list.add(info);

                        stringBuffer.append("\nid"+id+"\nname: " + info.name + "\nmobile number: " + info.mobileNumber+"\n**************************");
                    }

                    textView.setText(stringBuffer);

                    cursorInfo.close();
                }
            }
        }
        return list;
    }

    public String getContactName(Context context, String phoneNumber) {
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


}
