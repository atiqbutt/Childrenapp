package com.softvilla.childapp;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.List;

public class QRCode extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView;
    Thread thread ;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;
    public static int white = 0xFFFFFFFF;
    public static int black = 0xFF000000;
    SharedPreferences preferences;
    private PolicyManager policyManager;
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 100;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        policyManager = new PolicyManager(this);
        fillStats();


        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();
        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(QRCode.this);

        UpdateToken(preferences1.getString("childId",""),FirebaseInstanceId.getInstance().getToken());



        /*if(android.os.Build.VERSION.SDK_INT >= 21)
        {
            finishAndRemoveTask();
        }
        else
        {
            finish();
        }*/


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();


        /*if(!isMyServiceRunning(TrackingService.class,this)){
            startService(new Intent(this,TrackingService.class));
        }*/
       /* if(!preferences.getString("isFirst","").equalsIgnoreCase("1")){
            editor.putString("isFirst","1");
            editor.apply();
            Toast.makeText(this, "Set Pin", Toast.LENGTH_SHORT).show();
            *//*Intent intent = new Intent(this,SetPin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*//*
        }*/

        List<Utilities> utilities = Utilities.listAll(Utilities.class);

        if (utilities.size() == 0) {
            Utilities obj = new Utilities();
            obj.LATEST_CALL_LOG_ID = 0;
            obj.LATEST_SMS_ID = 0;
            obj.lastSmsSendTime = 0;
            obj.lastCallLogSendTime = 0;
            obj.save();
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        imageView = (ImageView)findViewById(R.id.qr);

        try {
            bitmap = TextToImageEncode(preferences.getString("child_id",""));

            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRBLACK):getResources().getColor(R.color.QRWHITE);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.deactivate_admin:
                if (policyManager.isAdminActive())
                    policyManager.disableAdmin();
                break;
        }

    }

    public void UpdateToken(final String childId,final String token){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(QRCode.this);
        AndroidNetworking.post("http://noorpublicschool.com/ApiPractice/UpdateToken")
                .addBodyParameter("token",token)
                .addBodyParameter("childId", preferences.getString("child_id", ""))
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        // do anything with response
                        PackageManager packageManager = getPackageManager();
                        ComponentName componentName = new ComponentName(QRCode.this,MainActivity.class);
                        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);

                    }

                    @Override
                    public void onError(ANError error) {

                        // handle error
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity", "resultCode " + resultCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:
                fillStats();
                break;
        }
    }

    private void requestPermission() {
        Toast.makeText(this, "Need to request permission", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
//        return ContextCompat.checkSelfPermission(this,
//                Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fillStats() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!hasPermission()) {


                requestPermission();
            }
        }
    }


}
