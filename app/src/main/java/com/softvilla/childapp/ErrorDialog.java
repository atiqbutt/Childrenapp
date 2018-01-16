package com.softvilla.childapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class ErrorDialog extends AppCompatActivity {
    List<ApplicationInfo> packages;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_dialog);
       /* Bundle extras = getIntent().getExtras();
        String appName = extras.getString("packageName");*/
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ErrorDialog.this, R.style.LightDialogTheme);
        builder.setMessage("Unfortunately Application has been Stopped. " )
               .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        ErrorDialog.this.finish();
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        //startActivity(new Intent(ChildrenList.this,MainManu.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        //finish();
                        //CurrentLocation.this.overridePendingTransition(0,0);
                    }
                });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        super.onBackPressed();
    }
}
