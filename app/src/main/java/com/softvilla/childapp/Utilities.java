package com.softvilla.childapp;

import com.orm.SugarRecord;

/**
 * Created by Hassan on 11/29/2017.
 */

public class Utilities extends SugarRecord {
    public Utilities(){

    }

    public long LATEST_SMS_ID, LATEST_CALL_LOG_ID ,LATEST_CONTACT_ID, lastSmsSendTime, lastCallLogSendTime;
}
