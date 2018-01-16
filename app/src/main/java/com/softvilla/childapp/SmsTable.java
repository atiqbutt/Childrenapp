package com.softvilla.childapp;

import com.orm.SugarRecord;

/**
 * Created by Hassan on 11/29/2017.
 */

public class SmsTable extends SugarRecord{
    public SmsTable(){

    }

    String phoneNumber, messageType, messageDate, messageBody, name;

    boolean isSend;
}
