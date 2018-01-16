package com.softvilla.childapp;

import com.orm.SugarRecord;

/**
 * Created by Hassan on 1/1/2018.
 */

public class SynchApps  extends SugarRecord{
    public SynchApps(){

    }
    public String appName, packageName, icon;
    boolean isInstall;
}
