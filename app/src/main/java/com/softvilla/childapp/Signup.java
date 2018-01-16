package com.softvilla.childapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void doSignup(View view) {
        String name=((EditText)findViewById(R.id.txt_name)).getText().toString();
        String email=((EditText)findViewById(R.id.txt_email)).getText().toString();
        String password=((EditText)findViewById(R.id.txt_password)).getText().toString();
        DoSignup doSignupObj=new DoSignup(this);
        doSignupObj.execute(name,email,password);
    }
}
