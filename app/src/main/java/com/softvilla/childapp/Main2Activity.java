package com.softvilla.childapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private PolicyManager policyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        policyManager = new PolicyManager(this);
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
}
