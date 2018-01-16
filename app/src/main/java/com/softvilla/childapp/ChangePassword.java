package com.softvilla.childapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

public class ChangePassword extends AppCompatActivity {

    EditText oldPassword, password, confirmPassword;
    String CHANGE_PASSWORD_URL = "https://teensafe.000webhostapp.com/Api/changePassword";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        AndroidNetworking.initialize(this);

        oldPassword = (EditText) findViewById(R.id.EditText_oldPwd);
        password = (EditText) findViewById(R.id.EditText_Pwd1);
        confirmPassword = (EditText) findViewById(R.id.EditText_Pwd2);
    }

    public void changePasswordClick(View view) {
        boolean isEmpty = false;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(TextUtils.isEmpty(oldPassword.getText().toString())){
            oldPassword.setError("Old Password");
            isEmpty = true;
        }

        if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("New Password");
            isEmpty = true;
        }

        if(TextUtils.isEmpty(confirmPassword.getText().toString())){
            confirmPassword.setError("Confirm Password");
            isEmpty = true;
        }

        if(!isEmpty){
            if(oldPassword.getText().toString().equalsIgnoreCase(preferences.getString("password",""))){
                if(password.getText().toString().equalsIgnoreCase(confirmPassword.getText().toString())){
                    final ProgressDialog dialog = new ProgressDialog(this);
                    dialog.setMessage("Changing Password...");
                    dialog.setCancelable(false);
                    AndroidNetworking.post(CHANGE_PASSWORD_URL)
                            .addBodyParameter("password", password.getText().toString())
                            .addBodyParameter("id", preferences.getString("userId",""))
                            .setTag("test")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    dialog.dismiss();
                                    Toast.makeText(ChangePassword.this, "Successfully Changed", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ChangePassword.this,Dashboard.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                    // do anything with response
                                }
                                @Override
                                public void onError(ANError error) {
                                    dialog.dismiss();
                                    Toast.makeText(ChangePassword.this, "No Network", Toast.LENGTH_SHORT).show();
                                    // handle error
                                }
                            });
                }
                else {
                    confirmPassword.setError("Wrong");
                }
            }
            else {
                oldPassword.setError("Wrong");
                Toast.makeText(this, "Wrong Old Password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
