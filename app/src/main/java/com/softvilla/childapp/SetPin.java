package com.softvilla.childapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

public class SetPin extends AppCompatActivity {

    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;
    boolean isEnter;
    String EnteredPin= "";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        mPinLockView.setPinLockListener(
                new PinLockListener() {
                    @Override
                    public void onComplete(String pin) {
                        if(isEnter){
                            if(EnteredPin.equalsIgnoreCase(pin)){
                                Toast.makeText(SetPin.this, "Successfully", Toast.LENGTH_SHORT).show();
                                editor.putString("pin",pin);
                                editor.apply();
                                editor.putBoolean("isPinSet",true);
                                editor.apply();
                                startActivity(new Intent(SetPin.this,Dashboard.class));
                                finish();
                            }
                            else {
                                isEnter = false;
                                //mPatternLockView.setViewMode(2);
                                Toast.makeText(SetPin.this, "Wrong Enter Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            mPinLockView.resetPinLockView();
                            EnteredPin = pin;
                            Toast.makeText(SetPin.this, "Enter Again", Toast.LENGTH_SHORT).show();
                            isEnter = true;
                        }

                    }

                    @Override
                    public void onEmpty() {

                    }

                    @Override
                    public void onPinChange(int pinLength, String intermediatePin) {

                    }
                }
        );
    }


}
