package com.softvilla.childapp;

import com.orm.SugarRecord;

/**
 * Created by Hassan on 11/29/2017.
 */

public class CallLogDetail extends SugarRecord {
    public CallLogDetail(){

    }
    /*public CallLogDetail(String phoneNumber, String callDate, String callType, String callDuration, String name){
        this.callDate = callDate;
        this.callDuration = callDuration;
        this.callType = callType;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }*/
    String phoneNumber, callType, callDate, callDuration, name;

    boolean isSend;
}
